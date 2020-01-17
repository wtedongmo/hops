package com.afsoltech.hops.web.controller

import com.afsoltech.core.exception.RestException
import com.afsoltech.core.util.enforce
import com.afsoltech.hops.core.model.notice.NoticeResponses
import com.afsoltech.hops.core.model.attribute.FieldsAttribute
import com.afsoltech.hops.core.model.integration.OutSelectedNoticeRequestDto
import com.afsoltech.hops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.hops.service.integration.RetrieveSelectedUnpaidNoticeService

import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.MessageSource
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("\${api.internal.customs.epayment.retrieveSelectedNotice}")
class RetrieveSelectedNoticeController(val retrieveSelectedUnpaidNoticeService: RetrieveSelectedUnpaidNoticeService){
    companion object : KLogging()

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource

    @GetMapping  //
    fun retrieveSelectedNotice(@RequestParam taxpayerNumber: String, request: HttpServletRequest): NoticeResponses<UnpaidNoticeResponseDto> {

        try {
            enforce(!taxpayerNumber.isNullOrBlank())
            val selectedRequest= OutSelectedNoticeRequestDto(taxpayerNumber)
            val sessionId = request.session.id
            val userLogin = request.session.getAttribute(FieldsAttribute.LOGIN.name) as String
            val result = retrieveSelectedUnpaidNoticeService.listSelectedUnpaidNotice(selectedRequest, userLogin, request)
            logger.trace { result }
            return result
        }catch (ex: RestException){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return NoticeResponses<UnpaidNoticeResponseDto>(
                    "F",
                    messageSource.getMessage(ex.message ?: "", ex.parameters.toTypedArray(), ex.message, ex.locale))
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return NoticeResponses<UnpaidNoticeResponseDto>(
                    "E",
                    ex.message,
                    null
            )
        }
    }
}