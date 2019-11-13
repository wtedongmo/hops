package com.nanobnk.epayment.core.web

import com.nanobnk.epayment.model.inbound.*
import com.nanobnk.epayment.service.ListUnpaidNoticeService
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
@RequestMapping("\${api.epayment.rest.listUnpaidNoticeUrl}")
class ListUnpaidNoticeController{
    companion object : KLogging()

    @Autowired
    lateinit var listUnpaidNoticeService: ListUnpaidNoticeService

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
                    "E",
                    messageSource.getMessage(ex.message, ex.parameters.toTypedArray(), ex.message, ex.locale))
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