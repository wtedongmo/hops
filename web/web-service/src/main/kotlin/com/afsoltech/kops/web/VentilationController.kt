package com.nanobnk.epayment.core.web

import com.nanobnk.epayment.model.inbound.*
import com.nanobnk.epayment.service.ListPaidNoticeService
import com.nanobnk.epayment.service.NotificationOfVentilationService
import com.nanobnk.util.rest.error.RestException
import com.nanobnk.util.rest.util.enforce
import com.nanobnk.util.rest.util.ensureNotNull
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.MessageSource
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("\${api.epayment.rest.notifyVentilationUrl}")
class VentilationController{
    companion object : KLogging()

    @Autowired
    lateinit var notificationOfVentilationService: NotificationOfVentilationService

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource

    @PostMapping  //
    fun notifyVentilation(@RequestBody notificationOfVentilationRequest: NotificationOfVentilationRequest, request: HttpServletRequest?)
            : NotificationOfVentilationResponse? {

        try {
            enforce(!notificationOfVentilationRequest.bankPaymentNumber.isBlank())
            enforce(!notificationOfVentilationRequest.ventilationStatus.name.isBlank())
            val result = notificationOfVentilationService.notifyVentilation(notificationOfVentilationRequest, request)
            logger.trace { result }
            return result
        }catch (ex: RestException){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return NotificationOfVentilationResponse("F",
                    messageSource.getMessage(ex.message, ex.parameters.toTypedArray(), ex.message, ex.locale),
                    notificationOfVentilationRequest.bankPaymentNumber

                    )
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return NotificationOfVentilationResponse(
                    "E",
                    ex.message,
                    notificationOfVentilationRequest.bankPaymentNumber
            )
        }
    }

}