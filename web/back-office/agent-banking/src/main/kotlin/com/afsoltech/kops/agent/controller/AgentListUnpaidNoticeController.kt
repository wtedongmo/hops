package com.afsoltech.kops.agent.controller

import com.afsoltech.core.service.utils.StringDateFormaterUtils
import com.afsoltech.kops.core.model.notice.UnpaidNoticeRequestDto
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.core.model.notice.AuthRequestDto
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import mu.KLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/agent-banking/", "/agent-banking/list-unpaid-customs", "/")
class AgentListUnpaidNoticeController(val listUnpaidNoticeService: ListUnpaidNoticeService){
    companion object : KLogging()

//    @Value("\${epayment.portal.link.code.toreplace}")
//    lateinit var userNiuCode: String

    @GetMapping
    fun unPaidNoticeForm(@RequestParam(value = "error", required = false) error: Boolean?,
                         @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                         request: HttpServletRequest): ModelAndView {
//        @RequestParam(value = "lang", required = false) lang: Locale?, res: HttpServletResponse
        val model = ModelAndView()

        val auth = SecurityContextHolder.getContext().authentication

        error?.let {
            if (error) model.addObject("errorMessage", "bad.information.provided")
        }
        errorMessage?.let {
            model.addObject("errorMessage", errorMessage)
        }
        model.addObject("username", auth.name)
        model.addObject("unpaidNoticeForm", UnpaidNoticeRequestDto())
        model.addObject("parentMenuHighlight", "notices-index")
        model.addObject("menuHighlight", "notices-unpaid")
        model.viewName = "agent-banking/unpaid-customs-form"

        return model
    }

    @PostMapping
    fun retrieveListPaidNotice(@ModelAttribute("unpaidNoticeForm") webRequest: UnpaidNoticeRequestDto, request: HttpServletRequest): ModelAndView { //

        val auth = SecurityContextHolder.getContext().authentication
        val noticeRequest = UnpaidNoticeRequestDto(
                webRequest.noticeNumber,
                webRequest.notificationDate?.replace("-", "")?.trim(),
                webRequest.taxpayerNumber,
                webRequest.taxpayerRepresentativeNumber,
                webRequest.dueDate?.replace("-", "")
        )
        var listUnPaidNotice = listUnpaidNoticeService.listUnpaidNotice(noticeRequest, null).result()
        listUnPaidNotice.forEach { item ->
            item.notificationDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.notificationDate)
            item.dueDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.dueDate)
        }
        logger.trace {"UnPaid Notice List "+ listUnPaidNotice }

        val modelAndView = ModelAndView()
        modelAndView.addObject("username", auth.name)
        modelAndView.addObject("UnpaidNotice", listUnPaidNotice)
        modelAndView.addObject("parentMenuHighlight", "notices-index")
        modelAndView.addObject("menuHighlight", "notices-unpaid")
        modelAndView.viewName = "agent-banking/list-unpaid-customs"
        return modelAndView
    }



}