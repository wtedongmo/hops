package com.afsoltech.kops.web.controller

import com.afsoltech.core.exception.RestException
import com.afsoltech.kops.core.model.attribute.FieldsAttribute
import com.afsoltech.kops.core.model.notice.FeeResponseDto
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
                    messageSource.getMessage(ex.message ?: "", ex.parameters.toTypedArray(), ex.message, ex.locale))
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