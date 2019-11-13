package com.nanobnk.epayment.service

import com.nanobnk.epayment.model.inbound.NotificationOfVentilationRequest
import com.nanobnk.epayment.model.inbound.NotificationOfVentilationResponse
import com.nanobnk.epayment.repository.PaymentRepository
import com.nanobnk.epayment.service.utils.CheckParticipantAPIRequest
import com.nanobnk.util.rest.error.BadRequestException
import org.springframework.stereotype.Service
import javax.servlet.http.HttpServletRequest

@Service
class NotificationOfVentilationService (val paymentRepositoty: PaymentRepository, val checkParticipantAPIRequest: CheckParticipantAPIRequest) {


    fun notifyVentilation(notificationOfVentilationRequest: NotificationOfVentilationRequest): NotificationOfVentilationResponse? {
        return notifyVentilation(notificationOfVentilationRequest)
    }

    @Synchronized
    fun notifyVentilation(notificationOfVentilationRequest: NotificationOfVentilationRequest, request: HttpServletRequest?): NotificationOfVentilationResponse?{

        checkParticipantAPIRequest.checkAPIRequest(request)
        val paymentEntity = paymentRepositoty.findByInboundPaymentNumber(notificationOfVentilationRequest.bankPaymentNumber)

        paymentEntity?.let {
            paymentEntity.ventilationStatus = notificationOfVentilationRequest.ventilationStatus
            paymentEntity.ventilationMessage = notificationOfVentilationRequest.ventilationMessage
            paymentRepositoty.save(paymentEntity)
            return NotificationOfVentilationResponse("S", "SUCCESS", notificationOfVentilationRequest.bankPaymentNumber)
        }
        throw BadRequestException("EPayment.Error.Notification.Ventilation.BankPaymentNumber.NotExists", listOf(notificationOfVentilationRequest.bankPaymentNumber))
    }
}