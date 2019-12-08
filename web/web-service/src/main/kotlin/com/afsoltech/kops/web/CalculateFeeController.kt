package com.nanobnk.epayment.core.web

import com.afsoltech.core.exception.RestException
import com.afsoltech.core.util.enforce
import com.afsoltech.kops.core.model.*
import com.afsoltech.kops.core.model.attribute.FieldsAttribute
import com.afsoltech.kops.core.model.integration.OutSelectedNoticeRequestDto
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import com.afsoltech.kops.service.integration.RetrieveSelectedUnpaidNoticeService
import com.afsoltech.kops.service.ws.CalculateFeeNoticeService

import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.MessageSource
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("\${api.internal.customs.epayment.calulateFee}")
class CalculateFeeController(val calculateFeeNoticeService: CalculateFeeNoticeService){
    companion object : KLogging()

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource

    @GetMapping  //
    fun calculateFee(request: HttpServletRequest): FeeResponseDto {

        try {
            val sessionId = request.session.id
            val userLogin = request.session.getAttribute(FieldsAttribute.LOGIN.name) as String
            val feeDto = calculateFeeNoticeService.calculateFeeNotice(userLogin)
            return FeeResponseDto("S", "SUCCES", feeDto)
        }catch (ex: RestException){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return FeeResponseDto(
                    "F",
                    messageSource.getMessage(ex.message?:"", ex.parameters.toTypedArray(), ex.message, ex.locale))
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return FeeResponseDto(
                    "E",
                    ex.message,
                    null
            )
        }
    }
}