package com.afsoltech.epayment.service

import com.afsoltech.core.entity.cap.temp.PäymentResultCode
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.exception.RestException
import com.afsoltech.core.exception.UnauthorizedException
import com.afsoltech.core.model.attribute.PaymentStatus
import com.afsoltech.core.repository.cap.PaymentRepository
import com.afsoltech.core.service.utils.LoadSettingDataToMap
import com.afsoltech.kops.core.model.integration.VentilationRequest
import com.afsoltech.kops.core.model.integration.VentilationResponse
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest

@Service
class VentilationService (val restTemplate: RestTemplate, val paymentRepository: PaymentRepository) {

    companion object : KLogging()

    @Value("\${api.external.customs.epayment.notifyVentilationUrl}")
    private lateinit var notifyVentilationUrl: String

    @Synchronized
    fun notifyVentilation(ventilationRequest: VentilationRequest, request: HttpServletRequest?): VentilationResponse{

        val payment = paymentRepository.findOneByPaymentNumber(ventilationRequest.bankPaymentNumber).get() ?:
            throw BadRequestException("Error.Ventilation.BankPaymentNumber.NotExists", listOf(ventilationRequest.bankPaymentNumber))

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val bankApiKey = LoadSettingDataToMap.settingMap.get("app.bank.epayment.apikey") ?:
        throw UnauthorizedException("Error.Payment.Parameter.ApiKey.NotFound")
        headers.add("apikey", bankApiKey.value)
        //val paymentRequestToSend = PaymentProcessMapper.ModelMapPaymentProcess.map(paymentRequest)
        val httpEntity = HttpEntity(ventilationRequest, headers)
        var response : ResponseEntity<VentilationResponse>? = null
        try {
            response = restTemplate.exchange(notifyVentilationUrl, HttpMethod.POST, httpEntity,
                    object : ParameterizedTypeReference<VentilationResponse>() {})
        }catch (ex: Exception){
            payment.paymentStatus = PaymentStatus.VENTIL_EXCEPTION
            paymentRepository.save(payment)
            logger.error { "Exception:"+ex.message+ "\n"+ ex.printStackTrace()}
            throw ex
        }

        val httpStatus = response.statusCode
        val responseVent = response.body

        if (!httpStatus.toString().startsWith("2") || responseVent == null) {
            payment.paymentStatus = PaymentStatus.VENTIL_ERROR
            paymentRepository.save(payment)
            if (!httpStatus.toString().startsWith("2"))
                throw RestException(httpStatus, httpStatus.reasonPhrase)
            if (responseVent == null)
                throw BadRequestException("Error.Payment.Ventil.Error")
        }

        if(!responseVent?.resultCode!!.equals(PäymentResultCode.S.name)){
            throw BadRequestException(responseVent.resultMessage)
        }
        payment.ventilationStatus = ventilationRequest.ventilationStatus
        payment.ventilationMessage = ventilationRequest.ventilationMessage
        paymentRepository.save(payment)
        return VentilationResponse("S", "SUCCESS", ventilationRequest.bankPaymentNumber)

    }
}