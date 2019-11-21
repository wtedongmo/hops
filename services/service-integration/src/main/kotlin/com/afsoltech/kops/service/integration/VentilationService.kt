package com.nanobnk.epayment.service

import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.exception.RestException
import com.afsoltech.core.exception.UnauthorizedException
import com.afsoltech.core.model.attribute.PaymentStatus
import com.afsoltech.core.repository.PaymentRepository
import com.afsoltech.kops.core.model.integration.PaymentProcessResponseDto
import com.afsoltech.kops.core.model.integration.VentilationRequest
import com.afsoltech.kops.core.model.integration.VentilationResponse
import com.afsoltech.kops.service.integration.PaymentOfSelectedNoticesService
import com.afsoltech.kops.service.utils.LoadBaseDataToMap
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

    @Value("\${api.epayment.customs.notifyVentilationUrl}")
    lateinit var notifyVentilationUrl: String

    @Synchronized
    fun notifyVentilation(ventilationRequest: VentilationRequest, request: HttpServletRequest?): VentilationResponse?{

        val payment = paymentRepository.findByPaymentNumber(ventilationRequest.bankPaymentNumber) ?:
            throw BadRequestException("Kops.Error.Ventilation.BankPaymentNumber.NotExists", listOf(ventilationRequest.bankPaymentNumber))

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val bankApiKey = LoadBaseDataToMap.parameterDataMap.get("api.epayment.bank.apikey") ?:
        throw UnauthorizedException("Kops.Error.Payment.Parameter.ApiKey.NotFound")
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
                throw BadRequestException("Kops.Error.Payment.Ventil.Error")
        }
        payment.ventilationStatus = ventilationRequest.ventilationStatus
        payment.ventilationMessage = ventilationRequest.ventilationMessage
        paymentRepository.save(payment)
        return VentilationResponse("S", "SUCCESS", ventilationRequest.bankPaymentNumber)

    }
}