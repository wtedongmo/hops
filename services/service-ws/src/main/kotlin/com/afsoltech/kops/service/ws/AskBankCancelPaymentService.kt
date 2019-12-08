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
import com.afsoltech.core.service.utils.LoadBaseDataToMap
import com.afsoltech.kops.core.model.AskBankCancelPaymentRequestDto
import com.afsoltech.kops.core.model.AskBankCancelPaymentResponseDto
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate

@Service
class AskBankCancelPaymentService(val restTemplate: RestTemplate) {

    companion object : KLogging()

    @Autowired
    lateinit var tempPaymentRepository: TempPaymentRepository

    @Value("\${api.external.bank.epayment.askBankCancelePaymentUrl}")
    lateinit var askBankCancelePaymentUrl: String

    /**
     * To request to bank to reserve fund of transaction if customer has enough money
     */
    @Transactional
    @Synchronized
    fun askBankCancelPayment(user: UserApp, tempPayment: TempPayment) : AskBankCancelPaymentResponseDto { //:Boolean

        val txDate = StringDateFormaterUtils.DateTimeToString.format(tempPayment.paymentDate)
        val askBankCancelPaymentRequestDto = AskBankCancelPaymentRequestDto(opCode=tempPayment.operationCode!!, acntNo = tempPayment.payerAccountNumber!!,
                providerCode = tempPayment.providerCode!!, customerNo = tempPayment.customerNumber!!, trxRefNo = tempPayment.internalPaymentNumber!!,
                trxDt = txDate!!, amount = tempPayment.amount!!, fee = tempPayment.feeAmount!!, totalAmount = tempPayment.totalAmount!!,
                currency = tempPayment.currency!!, billNumberList = tempPayment.billNumber!!.split(","), authCode = tempPayment.bankAuthNumber!!)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
//            headers.add("apikey", )
        val httpEntity = HttpEntity(askBankCancelPaymentRequestDto, headers)
        var response : ResponseEntity<AskBankCancelPaymentResponseDto>? = null
        try {
            response = restTemplate.exchange(askBankCancelePaymentUrl, HttpMethod.POST, httpEntity,
                    object : ParameterizedTypeReference<AskBankCancelPaymentResponseDto>() {})
        }catch (ex: Exception){
            tempPayment.paymentStatus = PaymentStatus.AUTH_CANCEL_ERROR
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
            tempPayment.bankAuthResultData = it.toString()
        }

        /*Update of payment status*/
        if(result.resultCode.equals(PäymentResultCode.S.name)) {
            if(result.resultData?.authRsltCd!!.equals(LoadBaseDataToMap.bankAuthCodeApproved!!))
                tempPayment.paymentStatus = PaymentStatus.AUTH_CANCEL
            else
                tempPayment.paymentStatus = PaymentStatus.AUTH_CANCEL_FAIL
        }else if (result.resultCode.equals(PäymentResultCode.F.name))
            tempPayment.paymentStatus = PaymentStatus.AUTH_CANCEL_FAIL
        else
            tempPayment.paymentStatus = PaymentStatus.AUTH_CANCEL_ERROR
        tempPaymentRepository.save(tempPayment)

        return result

    }

}