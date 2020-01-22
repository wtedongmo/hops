package com.afsoltech.core.controller

import com.afsoltech.core.exception.RestException
import com.afsoltech.core.service.utils.StringDateFormatterUtils
import com.afsoltech.core.service.utils.TranslateUtils
import com.afsoltech.hops.core.model.notice.AuthRequestDto
import com.afsoltech.hops.core.model.notice.NoticeRequestDto
import com.afsoltech.hops.core.model.notice.NoticeResponseDto
import com.afsoltech.hops.service.integration.ListPaidNoticeService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/portal/list-paid-customs")
class ListPaidNoticeController(val listPaidNoticeService: ListPaidNoticeService){
    companion object : KLogging()

    @Autowired
    lateinit var translateUtils: TranslateUtils

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
        model.viewName = "portal/paid-customs-form"
        return model
        //        return "signin";
    }



    @PostMapping
    fun getListPaidNotice(@ModelAttribute("paidNoticeForm") portalRequest: NoticeRequestDto, request: HttpServletRequest): ModelAndView { //

        try{
            val authCustoms = request.getSession().getAttribute("Auth_Customs") as AuthRequestDto?
            val auth = SecurityContextHolder.getContext().authentication
            val listPaidNotice :List<NoticeResponseDto>

            if(authCustoms==null){
                if(!portalRequest.noticeNumber.isNullOrBlank() && !portalRequest.taxpayerNumber.isNullOrBlank()){
                    portalRequest.notificationDate =  portalRequest.notificationDate?.replace("-", "")?.trim()
                    portalRequest.paymentDate =  portalRequest.paymentDate?.replace("-", "")?.trim()
                    listPaidNotice = listPaidNoticeService.listPaidNotice(portalRequest, request).result()
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
                    return ModelAndView("redirect:/portal/list-paid-customs?error=true");
                }
    //        val portalRequest = NoticePortalRequestDto()
    //        val paymentDate = portalRequest.paymentDate?.replace("-","")
                val noticeRequest = NoticeRequestDto(
                        portalRequest.noticeNumber,
                        portalRequest.notificationDate?.replace("-", ""),
                        nui,
                        representative,
                        portalRequest.paymentDate?.replace("-", "")
                )
                listPaidNotice = listPaidNoticeService.listPaidNotice(noticeRequest).result()
            }

            listPaidNotice.forEach { item ->
                item.notificationDate = StringDateFormatterUtils.StringDateToDateFormat.format(item.notificationDate)
                item.paymentDate = StringDateFormatterUtils.StringDateToDateFormat.formatPaidDate(item.paymentDate)
            }
            logger.trace {"Paid Notice List "+ listPaidNotice }

            val modelAndView = ModelAndView()
            modelAndView.addObject("username", auth.name)
            modelAndView.addObject("PaidNotice", listPaidNotice)
            modelAndView.addObject("parentMenuHighlight", "notices-index")
            modelAndView.addObject("menuHighlight", "notices-paid")
            modelAndView.viewName = "portal/list-paid-customs"
            return modelAndView

        }catch (ex: RestException){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/portal/list-paid-customs?errorMessage="+translateUtils.translate(ex.message?:""))
        }catch (ex: Exception){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/portal/list-paid-customs?errorMessage=admin.system.error")
        }

    }




}