package com.afsoltech.kops.service.ws

import com.afsoltech.core.entity.temp.PäymentResultCode
import com.afsoltech.core.entity.temp.TempPayment
import com.afsoltech.core.entity.user.UserApp
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.model.attribute.PaymentStatus
import com.afsoltech.core.repository.temp.TempPaymentRepository
import com.afsoltech.core.service.utils.StringDateFormaterUtils
import com.afsoltech.kops.core.model.AskBankAuthPaymentRequestDto
import com.afsoltech.kops.core.model.AskBankAuthPaymentResponseDto
import com.afsoltech.kops.core.repository.temp.SelectedNoticeRepository
import com.afsoltech.kops.service.utils.LoadBaseDataToMap
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@Service
class AskBankAuthPaymentService(val tempPaymentRepository: TempPaymentRepository, val restTemplate: RestTemplate) {

    companion object : KLogging()

    @Value("\${api.bank.epayment.askBankAuthorizePaymentUrl}")
    lateinit var askBankAuthorizePaymentUrl: String

    /**
     * To request to bank to reserve fund of transaction if customer has enough money
     */
    @Transactional
    @Synchronized
    fun askBankAuthPayment(user: UserApp, tempPayment: TempPayment) : PaymentStatus? { //:Boolean

        val txDate = StringDateFormaterUtils.DateTimeToString.format(tempPayment.paymentDate)
        val askBankPaymentRequestDto = AskBankAuthPaymentRequestDto(opCode=tempPayment.operationCode!!, acntNo = tempPayment.payerAccountNumber!!,
                providerCode = tempPayment.providerCode!!, customerNo = tempPayment.customerNumber!!, trxRefNo = tempPayment.internalPaymentNumber!!,
                trxDt = txDate!!, amount = tempPayment.amount!!, fee = tempPayment.feeAmount!!, totalAmount = tempPayment.totalAmount!!,
                currency = tempPayment.currency!!, billNumber = tempPayment.billNumber!!)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
//            headers.add("apikey", )
        val httpEntity = HttpEntity(askBankPaymentRequestDto, headers)
        var response : ResponseEntity<AskBankAuthPaymentResponseDto>? = null
        try {
            response = restTemplate.exchange(askBankAuthorizePaymentUrl, HttpMethod.POST, httpEntity,
                    object : ParameterizedTypeReference<AskBankAuthPaymentResponseDto>() {})
        }catch (ex: Exception){
            tempPayment.paymentStatus = PaymentStatus.AUTHORIZATION_ERROR
            tempPaymentRepository.save(tempPayment)
            logger.error { "Exception:"+ex.message+ "\n"+ ex.printStackTrace()}
            throw ex
        }

        tempPayment.bankResponseStatus = response.statusCode.value()
        val result = response.body?: throw BadRequestException("Kops.Error.Payment.Bank.Auth.Null")
        tempPayment.bankResultCode = result.resultCode
        tempPayment.bankResultMsg = result.resultMsg
        result.resultData?.let{
            tempPayment.bankAuthResultCode = it.authRsltCd
            tempPayment.bankAuthResultMessage = it.authRsltMsg
            tempPayment.bankAuthNumber = it.authRsltMsg
            tempPayment.bankAuthResultMessage = it.authCd
            tempPayment.bankAccNewBal = it.newBal
            tempPayment.bankAuthResultData = it.toString()
        }

        /*Update of payment status*/
        if(result.resultCode.equals(PäymentResultCode.S.name)) {
            if(result.resultData?.authRsltCd!!.equals(LoadBaseDataToMap.bankAuthCodeApproved!!))
                tempPayment.paymentStatus = PaymentStatus.AUTHORIZED
            else
                tempPayment.paymentStatus = PaymentStatus.NOT_AUTHORIZED
        }else if (result.resultCode.equals(PäymentResultCode.F.name))
            tempPayment.paymentStatus = PaymentStatus.NOT_AUTHORIZED
        else
            tempPayment.paymentStatus = PaymentStatus.AUTHORIZATION_ERROR
        tempPaymentRepository.save(tempPayment)

        return tempPayment.paymentStatus

    }

}