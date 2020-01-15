package com.afsoltech.kops.agent.controller

import com.afsoltech.kops.core.model.notice.NoticeRequestDto
import com.afsoltech.kops.core.model.notice.NoticeResponseDto
import com.afsoltech.kops.service.integration.ListPaidNoticeService
import mu.KLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/agent-banking/list-paid-customs")
class AgentListPaidNoticeController(val listPaidNoticeService: ListPaidNoticeService){
    companion object : KLogging()

    @GetMapping
    fun paidNoticeForm(@RequestParam(value = "error", required = false) error: Boolean?, request: HttpServletRequest): ModelAndView {

        val model = ModelAndView()

        val auth = SecurityContextHolder.getContext().authentication

//        model.addObject("noticesError", "error")
        error?.let {
            if (error) model.addObject("errorMessage", "bad.informations.provided")
        }
//        logger.info("username: " + auth.name)
        model.addObject("username", auth.name)
        model.addObject("paidNoticeForm", NoticeRequestDto())
        model.addObject("parentMenuHighlight", "notices-index")
        model.addObject("menuHighlight", "notices-paid")
        model.viewName = "agent-banking/paid-customs-form"
        return model
        //        return "signin";
    }



    @PostMapping
    fun getListPaidNotice(@ModelAttribute("paidNoticeForm") webRequest: NoticeRequestDto, request: HttpServletRequest): ModelAndView { //

        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name

        val noticeRequest = NoticeRequestDto(
                webRequest.noticeNumber,
                webRequest.notificationDate?.replace("-", ""),
                webRequest.taxpayerNumber,
                webRequest.taxpayerRepresentativeNumber,
                webRequest.paymentDate?.replace("-", "")
        )
        var listPaidNotice = listPaidNoticeService.listPaidNotice(noticeRequest).resultData
        listPaidNotice?.forEach { item ->
            item.notificationDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.notificationDate)
            item.paymentDate = StringDateFormaterUtils.StringDateToDateFormat.formatPaidDate(item.paymentDate)
        }
        logger.trace {"Paid Notice List "+ listPaidNotice }

        val modelAndView = ModelAndView()
        modelAndView.addObject("username", auth.name)
        modelAndView.addObject("PaidNotice", listPaidNotice?: emptyList<NoticeResponseDto>())
        modelAndView.addObject("parentMenuHighlight", "notices-index")
        modelAndView.addObject("menuHighlight", "notices-paid")
        modelAndView.viewName = "agent-banking/list-paid-customs"
        return modelAndView
    }




}