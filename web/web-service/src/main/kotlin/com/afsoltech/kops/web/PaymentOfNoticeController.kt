package com.nanobnk.epayment.core.web

import com.afsoltech.core.entity.temp.PäymentResultCode
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.exception.RestException
import com.afsoltech.core.repository.user.UserAppRepository
import com.afsoltech.core.service.utils.LoadBaseDataToMap
import com.afsoltech.kops.core.model.InitPaymentRequestDto
import com.afsoltech.kops.core.model.NoticeRequestDto
import com.afsoltech.kops.core.model.attribute.FieldsAttribute
import com.afsoltech.kops.core.model.integration.PaymentProcessResponseDto
import com.afsoltech.kops.service.integration.ListPaidNoticeService
import com.afsoltech.kops.service.ws.AskBankAuthPaymentService
import com.afsoltech.kops.service.ws.AskBankCancelPaymentService
import com.afsoltech.kops.service.ws.InitPaymentOfNoticeService
import com.afsoltech.kops.service.ws.KopsPaymentOfNoticeService

import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.MessageSource
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("\${api.internal.customs.epayment.paymentOfNotice}")
class PaymentOfNoticeController(val kopsPaymentOfNoticeService: KopsPaymentOfNoticeService, val userAppRepository: UserAppRepository){
    companion object : KLogging()

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource


    @PostMapping  //
    fun paymentOfNoticeProcess(@RequestBody initPaymentRequest: InitPaymentRequestDto, request: HttpServletRequest): PaymentProcessResponseDto {

        try {

            val userLogin = request.session.getAttribute(FieldsAttribute.LOGIN.name) as String
            val userApp = userAppRepository.findByLogin(userLogin)
            userApp?.let {
                val paymentResult = kopsPaymentOfNoticeService.paymentOfNotice(userApp, initPaymentRequest, request)

                return paymentResult
            }
            throw BadRequestException("Kops.Error.User.NotFound", listOf(userLogin))
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
