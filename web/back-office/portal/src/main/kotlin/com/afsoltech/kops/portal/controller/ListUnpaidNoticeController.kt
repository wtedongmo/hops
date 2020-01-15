package com.afsoltech.core.controller

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
@RequestMapping("/portal/list-unpaid-customs")
class ListUnpaidNoticeController(val listUnpaidNoticeService: ListUnpaidNoticeService){
    companion object : KLogging()

//    @Value("\${epayment.portal.link.code.toreplace}")
//    lateinit var userNiuCode: String

    @GetMapping
    fun unPaidNoticeForm(@RequestParam(value = "error", required = false) error: Boolean?, request: HttpServletRequest): ModelAndView {
//        @RequestParam(value = "lang", required = false) lang: Locale?, res: HttpServletResponse
        val model = ModelAndView()

        val auth = SecurityContextHolder.getContext().authentication

//        val authCustoms = request.getSession().getAttribute("Auth_Customs") as AuthRequestDto?
//        if(authCustoms==null){
//            model.viewName = "redirect:/portal/auth-customs-user?errorMessage=app.auth.customs.required"
//            return model
//        }

        error?.let {
            if (error) model.addObject("errorMessage", "bad.information.provided")
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

        val authCustoms = request.getSession().getAttribute("Auth_Customs") as AuthRequestDto?
        val auth = SecurityContextHolder.getContext().authentication
        val listUnPaidNotice : List<UnpaidNoticeResponseDto>

        if(authCustoms==null){
            if(!portalRequest.noticeNumber.isNullOrBlank() && !portalRequest.taxpayerNumber.isNullOrBlank()){
                portalRequest.notificationDate =  portalRequest.notificationDate?.replace("-", "")?.trim()
                portalRequest.dueDate =  portalRequest.dueDate?.replace("-", "")?.trim()
                listUnPaidNotice = listUnpaidNoticeService.listUnpaidNotice(portalRequest, request).result()
            }else
                return ModelAndView("redirect:/portal/auth-customs-user?errorMessage=app.auth.customs.required")
        }else {

            val username = auth.name
            val nuiUser = authCustoms.taxpayerNumber!! //username.split("#").first()
            val nui = if (portalRequest.taxpayerNumber.isNullOrBlank()) nuiUser
            else portalRequest.taxpayerNumber

            val representative = if (!portalRequest.taxpayerRepresentativeNumber.isNullOrBlank()) portalRequest.taxpayerRepresentativeNumber
            else if (!nui.equals(nuiUser, true)) nuiUser
            else ""

            if (!nui.equals(nuiUser, true) && !representative.equals(nuiUser, true)) {
                return ModelAndView("redirect:/portal/list-unpaid-customs?error=true");
            }
//        val portalRequest = NoticePortalRequestDto()
            val noticeRequest = UnpaidNoticeRequestDto(
                    portalRequest.noticeNumber,
                    portalRequest.notificationDate?.replace("-", "")?.trim(),
                    nui,
                    representative,
                    portalRequest.dueDate?.replace("-", "")
            )
            listUnPaidNotice = listUnpaidNoticeService.listUnpaidNotice(noticeRequest, null).result()
        }

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
        modelAndView.viewName = "portal/list-unpaid-customs"
        return modelAndView
    }



}