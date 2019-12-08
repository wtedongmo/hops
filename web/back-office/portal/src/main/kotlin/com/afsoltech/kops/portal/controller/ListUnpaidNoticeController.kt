package com.afsoltech.kops.portal.controller

import com.afsoltech.core.service.AccountBankService
import com.afsoltech.core.service.utils.StringDateFormaterUtils
import com.afsoltech.kops.core.model.UnpaidNoticeRequestDto
import com.afsoltech.kops.core.model.attribute.FieldsAttribute
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import com.afsoltech.kops.service.ws.CalculateFeeNoticeService
import com.afsoltech.kops.service.ws.SaveSelectedNoticeService
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/portal/","/portal/list-unpaid-customs")
class ListUnpaidNoticeController(val listUnpaidNoticeService: ListUnpaidNoticeService){
    companion object : KLogging()

//    @Value("\${epayment.portal.link.code.toreplace}")
//    lateinit var userNiuCode: String

    @GetMapping
    fun unPaidNoticeForm(@RequestParam(value = "error", required = false) error: Boolean?, request: HttpServletRequest): ModelAndView {
//        @RequestParam(value = "lang", required = false) lang: Locale?, res: HttpServletResponse
        val model = ModelAndView()

        val auth = SecurityContextHolder.getContext().authentication

//        model.addObject("noticesError", "error")

//        logger.info("username: " + auth.name)
        error?.let {
            if (error) model.addObject("errorMessage", "bad.informations.provided")
        }
        model.addObject("username", auth.name)
        model.addObject("unpaidNoticeForm", UnpaidNoticeRequestDto())
        model.addObject("parentMenuHighlight", "notices-index")
        model.addObject("menuHighlight", "notices-unpaid")
        model.viewName = "portal/unpaid-customs-form"

//        lang?.let {
//            res.locale = lang
//        }
        return model
    }



    @PostMapping
    fun retrieveListPaidNotice(@ModelAttribute("unpaidNoticeForm") portalRequest: UnpaidNoticeRequestDto, request: HttpServletRequest): ModelAndView { //

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
        val noticeRequest = UnpaidNoticeRequestDto(
                portalRequest.noticeNumber,
                portalRequest.notificationDate?.replace("-",""),
                nui,
                representative,
                portalRequest.dueDate?.replace("-","")
        )
        var listUnPaidNotice = listUnpaidNoticeService.listUnpaidNotice(noticeRequest, null).resultData
        listUnPaidNotice?.forEach { item ->
            item.notificationDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.notificationDate)
            item.dueDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.dueDate)
        }
        ListPaidNoticeController.logger.trace {"UnPaid Notice List "+ listUnPaidNotice }

        val modelAndView = ModelAndView()
        modelAndView.addObject("username", auth.name)
        modelAndView.addObject("UnpaidNotice", listUnPaidNotice?: emptyList<UnpaidNoticeResponseDto>())
        modelAndView.addObject("parentMenuHighlight", "notices-index")
        modelAndView.addObject("menuHighlight", "notices-unpaid")
        modelAndView.viewName = "portal/list-unpaid-customs"
        return modelAndView
    }



}