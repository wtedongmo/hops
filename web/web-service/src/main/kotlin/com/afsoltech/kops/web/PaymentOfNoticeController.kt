package com.nanobnk.epayment.core.web

import com.nanobnk.epayment.model.inbound.*
import com.nanobnk.epayment.service.ListPaidNoticeService
import com.nanobnk.epayment.service.PaymentProcessOfNoticesService
import com.nanobnk.util.rest.error.BadRequestException
import com.nanobnk.util.rest.error.ConflictException
import com.nanobnk.util.rest.error.ErrorController
import com.nanobnk.util.rest.error.RestException
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
import java.util.*

@RestController
@RequestMapping("\${api.epayment.rest.paymentOfNoticeURL}")
class PaymentOfNoticeController(val paymentNoticeProcessService: PaymentProcessOfNoticesService){
    companion object : KLogging()

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource


    @PostMapping  //
    fun paymentOfNoticeProcess(@RequestBody paymentRequest: PaymentProcessRequestDto): PaymentProcessResponseDto {

        try {
            val paidNotice = paymentNoticeProcessService.paymentOfNoticeProcess(paymentRequest)
            logger.trace { paidNotice }
            return paidNotice!!
        }catch (ex: RestException){
            logger.error(ex.message, ex)
            return PaymentProcessResponseDto(
                    "E",
                    "Echec",
                    null, paymentRequest.bankPaymentNumber, "E",
                    messageSource.getMessage(ex.message, ex.parameters.toTypedArray(), ex.message, ex.locale))
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return PaymentProcessResponseDto(
                    "E",
                    "Echec",
                    null, paymentRequest.bankPaymentNumber, "E", ex.message)
        }

  }




}
