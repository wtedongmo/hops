package com.nanobnk.epayment.core.web

import com.afsoltech.core.exception.RestException
import com.afsoltech.core.util.enforce
import com.afsoltech.kops.core.model.NoticeRequestDto
import com.afsoltech.kops.core.model.NoticeResponseDto
import com.afsoltech.kops.core.model.NoticeResponses
import com.afsoltech.kops.service.integration.ListPaidNoticeService

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
@RequestMapping("\${api.internal.customs.epayment.listPaidNotice}")
class ListPaidNoticeController(val listPaidNoticeService: ListPaidNoticeService){
    companion object : KLogging()

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource

    @PostMapping  //
    fun getListPaidNotice(@RequestBody noticeRequest: NoticeRequestDto, request: HttpServletRequest?): NoticeResponses<NoticeResponseDto> {

        try {
            enforce(!noticeRequest.taxpayerNumber.isNullOrBlank())
            val result = listPaidNoticeService.listPaidNotice(noticeRequest, request)
            logger.trace { result }
            return result
        }catch (ex: RestException){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return NoticeResponses<NoticeResponseDto>(
                    "F",
                    messageSource.getMessage(ex.message?:"", ex.parameters.toTypedArray(), ex.message, ex.locale))
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return NoticeResponses<NoticeResponseDto>(
                    "E",
                    ex.message,
                    null
            )
        }

    }




}