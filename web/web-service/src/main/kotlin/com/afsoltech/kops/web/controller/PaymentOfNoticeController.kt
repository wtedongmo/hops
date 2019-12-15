package com.afsoltech.kops.web.controller

import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.exception.RestException
import com.afsoltech.core.repository.user.UserAppRepository
import com.afsoltech.kops.core.model.InitPaymentRequestDto
import com.afsoltech.kops.core.model.attribute.FieldsAttribute
import com.afsoltech.kops.core.model.integration.PaymentProcessResponseDto
import com.afsoltech.kops.service.ws.KopsPaymentOfNoticeService

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
@RequestMapping("\${api.internal.customs.epayment.paymentOfNotice}")
class PaymentOfNoticeController(val kopsPaymentOfNoticeService: KopsPaymentOfNoticeService){
    companion object : KLogging()

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource


    @PostMapping  //
    fun paymentOfNoticeProcess(@RequestBody initPaymentRequest: InitPaymentRequestDto, request: HttpServletRequest): PaymentProcessResponseDto {

        try {

            val userLogin = request.session.getAttribute(FieldsAttribute.LOGIN.name) as String
//            val userAppOp = userAppRepository.findByUsername(userLogin)
//            if(userAppOp.isPresent) {
//                val userApp = userAppOp.get()
                val paymentResult = kopsPaymentOfNoticeService.paymentOfNotice(userLogin, initPaymentRequest, request)

                return paymentResult
//            }
//            throw BadRequestException("Error.User.NotFound", listOf(userLogin))
        }catch (ex: RestException){
            logger.error(ex.message, ex)
            return PaymentProcessResponseDto(
                    "F",
                    "Echec",
                    null, null, "F",
                    messageSource.getMessage(ex.message?:"", ex.parameters.toTypedArray(), ex.message, ex.locale))
        }catch (ex: Exception){
            logger.error(ex.message, ex)
            return PaymentProcessResponseDto(
                    "E",
                    "Error",
                    null, null, "E", ex.message)
        }

  }




}
