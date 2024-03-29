package com.afsoltech.hops.agent.controller

import com.afsoltech.core.service.utils.StringDateFormatterUtils
import com.afsoltech.hops.core.model.notice.NoticeRequestDto
import com.afsoltech.hops.core.model.notice.NoticeResponseDto
import com.afsoltech.hops.service.integration.ListPaidNoticeService
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
    fun paidNoticeForm(@RequestParam(value = "error", required = false) error: Boolean?,
                       @RequestParam(value = "errorMessage", required = false) errorMessage: String?, request: HttpServletRequest): ModelAndView {

        val model = ModelAndView()

        val auth = SecurityContextHolder.getContext().authentication

//        model.addObject("noticesError", "error")
        error?.let {
            if (error) model.addObject("errorMessage", "bad.informations.provided")
        }
        errorMessage?.let {
            model.addObject("errorMessage", errorMessage)
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

        val modelAndView = ModelAndView()
        modelAndView.addObject("username", auth.name)
//        request.session.setAttribute("paidNoticeForm", webRequest)
        try {
            val listPaidNotice = listPaidNoticeService.listPaidNotice(noticeRequest).resultData
            listPaidNotice?.forEach { item ->
                item.notificationDate = StringDateFormatterUtils.StringDateToDateFormat.format(item.notificationDate)
                item.paymentDate = StringDateFormatterUtils.StringDateToDateFormat.formatPaidDate(item.paymentDate)
            }
            logger.trace {"Paid Notice List "+ listPaidNotice }
            modelAndView.addObject("PaidNotice", listPaidNotice?: emptyList<NoticeResponseDto>())
        }catch (ex: Exception){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/agent-banking/list-paid-customs?errorMessage="+(ex.message ?:"admin.system.error"))
        }

        modelAndView.addObject("parentMenuHighlight", "notices-index")
        modelAndView.addObject("menuHighlight", "notices-paid")
        modelAndView.viewName = "agent-banking/list-paid-customs"
        return modelAndView
    }




}