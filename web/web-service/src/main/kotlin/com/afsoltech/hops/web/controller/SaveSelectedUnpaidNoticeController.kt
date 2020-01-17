package com.afsoltech.hops.web.controller

import com.afsoltech.core.exception.RestException
import com.afsoltech.core.util.enforce
import com.afsoltech.hops.core.model.notice.NoticeResponses
import com.afsoltech.hops.core.model.attribute.FieldsAttribute
import com.afsoltech.hops.service.ws.SaveSelectedNoticeService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.MessageSource
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("\${api.internal.customs.epayment.saveCheckedUnpaidNoticeUrl}")
class SaveSelectedUnpaidNoticeController (val saveSelectedNoticeService: SaveSelectedNoticeService){
    companion object : KLogging()

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource

    @PostMapping  //
    fun saveSelectedUnpaidNotice(@RequestParam selectedNoticeList: List<String>, request: HttpServletRequest): NoticeResponses<String> {

        try {
            enforce(!selectedNoticeList.isEmpty())
            val sessionId = request.session.id
            val userLogin = request.session.getAttribute(FieldsAttribute.LOGIN.name) as String
            val result = saveSelectedNoticeService.saveSelectedNotices(userLogin, selectedNoticeList)
//            logger.trace { result }
            return NoticeResponses<String>("S", "SUCCES", null)
        }catch (ex: RestException){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return NoticeResponses<String>(
                    "F",
                    messageSource.getMessage(ex.message ?: "", ex.parameters.toTypedArray(), ex.message, ex.locale))
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return NoticeResponses<String>(
                    "E",
                    ex.message,
                    null
            )
        }
    }
}