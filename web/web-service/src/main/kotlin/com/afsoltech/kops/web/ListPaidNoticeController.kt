package com.nanobnk.epayment.core.web

import com.nanobnk.epayment.model.inbound.NoticeRequestDto
import com.nanobnk.epayment.model.inbound.NoticeResponseDto
import com.nanobnk.epayment.model.inbound.NoticeResponses
import com.nanobnk.epayment.model.inbound.PaymentProcessResponseDto
import com.nanobnk.epayment.service.ListPaidNoticeService
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
@RequestMapping("\${api.epayment.rest.listPaidNoticeUrl}")
class ListPaidNoticeController{
    companion object : KLogging()

    @Autowired
    lateinit var listPaidNoticeService: ListPaidNoticeService

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
                    "E",
                    messageSource.getMessage(ex.message, ex.parameters.toTypedArray(), ex.message, ex.locale))
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