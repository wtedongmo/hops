package com.afsoltech.hops.service.integration

import com.afsoltech.core.entity.cap.temp.TempPayment
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.exception.RestException
import com.afsoltech.core.exception.UnauthorizedException
import com.afsoltech.core.model.attribute.PaymentStatus
import com.afsoltech.core.repository.cap.temp.TempPaymentRepository
import com.afsoltech.core.service.utils.LoadSettingDataToMap
import com.afsoltech.hops.core.entity.customs.NoticeBeneficiary
import com.afsoltech.hops.core.model.integration.PaymentProcessRequestDto
import com.afsoltech.hops.core.model.integration.PaymentProcessResponseDto
import com.afsoltech.hops.core.repository.NoticeBeneficiaryRepository
import com.afsoltech.hops.core.repository.NoticeRepository
import com.afsoltech.hops.core.repository.PaymentOfNoticeRepository
import com.afsoltech.hops.core.repository.temp.SelectedNoticeRepository
import com.afsoltech.hops.service.mapper.NoticeModelToEntity
import com.afsoltech.epayment.service.mapper.NoticeEntityToEntity
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate


@Service
class PaymentOfSelectedNoticesService(
        val restTemplate: RestTemplate,
        val tempPaymentRepository: TempPaymentRepository
) {

    companion object : KLogging()

    @Value("\${api.external.customs.epayment.paymentOfNoticeURL}")
    private lateinit var paymentOfNoticeURL: String

//    @Value("\${app.bank.epayment.apikey}")
//    lateinit var bankApiKey: String

    /**
     * To Call ePayment API to pay the selected notices
     */

    fun paymentOfSelectedNotice(tempPayment: TempPayment, paymentRequest: PaymentProcessRequestDto): PaymentProcessResponseDto {

//        val selectedNoticeList = getListNoticeCheckExistAndAmount(paymentRequest.noticeList)

        /* Send Payment To EPAYMENT */
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        LoadSettingDataToMap.ePaymentApiKey?.let {
            headers.add("apikey", it)
            val httpEntity = HttpEntity(paymentRequest, headers)
            var response : ResponseEntity<PaymentProcessResponseDto>? = null
            try {
                response = restTemplate.exchange(paymentOfNoticeURL, HttpMethod.POST, httpEntity,
                        object : ParameterizedTypeReference<PaymentProcessResponseDto>() {})
            }catch (ex: Exception){
                tempPayment.paymentStatus = PaymentStatus.PAYMENT_EXCEPTION
                tempPaymentRepository.save(tempPayment)
                logger.error { "Exception:"+ex.message+ "\n"+ ex.printStackTrace()}
                throw ex
            }

            val httpStatus = response.statusCode
            tempPayment.remoteResponseStatus = httpStatus.value()

            val responsePayment = response.body
            logger.trace { "\tePayment Responses \n\t $responsePayment" }

            if (!httpStatus.toString().startsWith("2") || responsePayment == null) {
                tempPayment.paymentStatus = PaymentStatus.PAYMENT_ERROR
                tempPaymentRepository.save(tempPayment)
                if (!httpStatus.toString().startsWith("2"))
                    throw RestException(httpStatus, httpStatus.reasonPhrase)
                if (responsePayment == null)
                    throw BadRequestException("Error.Parameter.Value")
            }

            /*Update epayment database payment and customs checking response  */
            val tempPaymentSaved = updateTempPaymentFromResponse(responsePayment, tempPayment)

            return responsePayment
        }
        throw UnauthorizedException("Error.Payment.Parameter.ApiKey.NotFound")
    }


    private fun updateTempPaymentFromResponse(responsePayment: PaymentProcessResponseDto?, tempPayment: TempPayment): TempPayment {

        tempPayment.remoteResultCode = responsePayment?.result
        tempPayment.remoteResultMessage = responsePayment?.message
        tempPayment.remotePaymentId = responsePayment?.epaymentId
        tempPayment.remotePaymentResultCode = responsePayment?.paymentResultCode
        tempPayment.remotePaymentResultMessage = responsePayment?.paymentResultMsg

        var status = PaymentStatus.IN_PROGRESS
        if (responsePayment?.result.equals("F", true)) {
            responsePayment?.paymentResultCode.let {
                status = when (it) {
                    "02" -> PaymentStatus.NUMBER_EXISTS
                    "03" -> PaymentStatus.NO_LIST
                    "04" -> PaymentStatus.AMOUNT_ERROR
                    else -> PaymentStatus.FAILED
                }
            }

        } else if (responsePayment?.result.equals("E", true))
            status = PaymentStatus.SYSTEM_ERROR
        else if (responsePayment?.result.equals("S", true)) {
            if (responsePayment?.paymentResultCode.equals("1"))
                status = PaymentStatus.COMPLETED
            else status = PaymentStatus.FAILED
        }else
            status = PaymentStatus.UNKNOWN_ERROR

        tempPayment.paymentStatus = status

        val tempPaymentSaved = tempPaymentRepository.save(tempPayment)

        return tempPaymentSaved
    }



}

