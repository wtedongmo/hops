package com.afsoltech.kops.portal.controller

import com.afsoltech.core.service.utils.StringDateFormaterUtils
import com.afsoltech.kops.core.model.NoticeRequestDto
import com.afsoltech.kops.core.model.NoticeResponseDto
import com.afsoltech.kops.service.integration.ListPaidNoticeService
import mu.KLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@RestController
@RequestMapping("/portal/list-paid-customs")
class ListPaidNoticeController(val listPaidNoticeService: ListPaidNoticeService){
    companion object : KLogging()

    @GetMapping
    fun paidNoticeForm(@RequestParam(value = "error", required = false) error: Boolean?): ModelAndView {
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
        model.viewName = "portal/paid-customs-form"
        return model
        //        return "signin";
    }



    @PostMapping
    fun getListPaidNotice(@ModelAttribute("paidNoticeForm") portalRequest: NoticeRequestDto): ModelAndView { //

        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name
        val nuiUser = username.split("#").first()
        val nui = if(portalRequest.taxpayerNumber.isNullOrBlank()) nuiUser
                else portalRequest.taxpayerNumber

        val representative = if(!portalRequest.taxpayerRepresentativeNumber.isNullOrBlank()) portalRequest.taxpayerRepresentativeNumber
                    else if(!nui.equals(nuiUser, true)) nuiUser
                    else ""

        if(!nui.equals(nuiUser, true) && !representative.equals(nuiUser, true)){
            return ModelAndView("redirect:/portal/list-unpaid-customs?error=true");
        }
//        val portalRequest = NoticePortalRequestDto()
//        val paymentDate = portalRequest.paymentDate?.replace("-","")
        val noticeRequest = NoticeRequestDto(
                portalRequest.noticeNumber,
                portalRequest.notificationDate?.replace("-",""),
                nui,
                representative,
                portalRequest.paymentDate?.replace("-","")
        )
        var listPaidNotice = listPaidNoticeService.listPaidNotice(noticeRequest).resultData
        listPaidNotice?.forEach { item ->
            item.notificationDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.notificationDate)
            item.paymentDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.paymentDate)
        }
        logger.trace {"Paid Notice List "+ listPaidNotice }

        val modelAndView = ModelAndView()
        modelAndView.addObject("username", auth.name)
        modelAndView.addObject("PaidNotice", listPaidNotice?: emptyList<NoticeResponseDto>())
        modelAndView.addObject("parentMenuHighlight", "notices-index")
        modelAndView.addObject("menuHighlight", "notices-paid")
        modelAndView.viewName = "portal/list-paid-customs"
        return modelAndView
    }




}