package com.nanobnk.epayment.core.web

import com.afsoltech.core.exception.RestException
import com.afsoltech.core.util.enforce
import com.afsoltech.kops.core.model.NoticeResponses
import com.afsoltech.kops.core.model.UnpaidNoticeRequestDto
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService

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
@RequestMapping("\${api.internal.customs.epayment.listUnpaidNotice}")
class ListUnpaidNoticeController(val listUnpaidNoticeService: ListUnpaidNoticeService){
    companion object : KLogging()

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource

    @PostMapping  //
    fun getListUnpaidNotice(@RequestBody noticeRequest: UnpaidNoticeRequestDto, request: HttpServletRequest?): NoticeResponses<UnpaidNoticeResponseDto> {

        try {
            enforce(!noticeRequest.taxpayerNumber.isNullOrBlank())
            val result = listUnpaidNoticeService.listUnpaidNotice(noticeRequest, request)
            logger.trace { result }
            return result
        }catch (ex: RestException){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return NoticeResponses<UnpaidNoticeResponseDto>(
                    "F",
                    messageSource.getMessage(ex.message?:"", ex.parameters.toTypedArray(), ex.message, ex.locale))
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