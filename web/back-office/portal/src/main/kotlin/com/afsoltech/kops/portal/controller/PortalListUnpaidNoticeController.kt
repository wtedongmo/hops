package com.nanobnk.epayment.portal.controller

import com.nanobnk.epayment.model.attribute.BankDto
import com.nanobnk.epayment.model.inbound.*
import com.nanobnk.epayment.portal.repository.ParticipantRepository
import com.nanobnk.epayment.portal.service.PortalListUnpaidNoticeService
import com.nanobnk.epayment.portal.utils.StringDateFormaterUtils
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.util.*
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/portal/","/portal/list-unpaid-customs")
class PortalListUnpaidNoticeController(val listUnpaidNoticeService: PortalListUnpaidNoticeService,
                                       val participantRepository: ParticipantRepository){
    companion object : KLogging()

    @Value("\${epayment.portal.link.code.toreplace}")
    lateinit var userNuiCode: String

    @GetMapping
    fun unPaidNoticeForm(@RequestParam(value = "error", required = false) error: Boolean?): ModelAndView {
//        @RequestParam(value = "lang", required = false) lang: Locale?, res: HttpServletResponse
        val model = ModelAndView()

        val auth = SecurityContextHolder.getContext().authentication

//        model.addObject("noticesError", "error")

//        logger.info("username: " + auth.name)
        error?.let {
            if (error) model.addObject("errorMessage", "bad.informations.provided")
        }
        model.addObject("username", auth.name)
        model.addObject("unpaidNoticeForm", UnpaidNoticePortalRequestDto())
        model.addObject("parentMenuHighlight", "notices-index")
        model.addObject("menuHighlight", "notices-unpaid")
        model.viewName = "portal/unpaid-customs-form"

//        lang?.let {
//            res.locale = lang
//        }
        return model
    }



    @PostMapping
    fun getListPaidNotice(@ModelAttribute("unpaidNoticeForm") portalRequest: UnpaidNoticePortalRequestDto): ModelAndView { //

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
        var listUnPaidNotice = listUnpaidNoticeService.listUnpaidNotice(noticeRequest)
        listUnPaidNotice?.forEach { item ->
            item.notificationDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.notificationDate)
            item.dueDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.dueDate)
        }
        PortalListPaidNoticeController.logger.trace {"UnPaid Notice List "+ listUnPaidNotice }

        val modelAndView = ModelAndView()
        modelAndView.addObject("username", auth.name)
        modelAndView.addObject("UnpaidNotice", listUnPaidNotice?: emptyList<UnpaidNoticePortalResponseDto>())
        modelAndView.addObject("parentMenuHighlight", "notices-index")
        modelAndView.addObject("menuHighlight", "notices-unpaid")
        modelAndView.viewName = "portal/list-unpaid-customs"
        return modelAndView
    }

    @PostMapping("save-checked-customs")
    fun saveCheckedNotices(@RequestParam("checkedNotices") checkedNotices: List<String>?): ModelAndView { //

        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name
        val nuiUser = username.split("#").first()

        logger.info("\nCheck Notices: " + checkedNotices)

        //Call Service to save checked Notices
        checkedNotices?.let {
            listUnpaidNoticeService.saveCheckedNotices(nuiUser, checkedNotices)
        }

        val participantList = participantRepository.findAll()
        val list = participantList.map { participant ->
            BankDto(participant.participantCode, participant.participantName!!, participant.paymentLink!!.replace(userNuiCode, nuiUser)) }

        val modelAndView = ModelAndView()
        modelAndView.addObject("username", auth.name)
        modelAndView.addObject("message", "choose.payment.partner")
        modelAndView.addObject("Partner", list)
        // menu highlight
        modelAndView.addObject("parentMenuHighlight", "bank-index")
        modelAndView.addObject("menuHighlight", "bank-list")
        modelAndView.viewName = "portal/list-partner"
        return modelAndView
    }



//    @GetMapping("/portal/list-unpaid-customs")  //
//    fun getListUnpaidNotice(): ModelAndView { // @RequestBody unpaidNoticePortalRequest: UnpaidNoticePortalRequestDto
//
//        val auth = SecurityContextHolder.getContext().authentication
//        val username= auth.name
//        val nui = username.split("#").first()
//
//        val unpaidNoticePortalRequest = UnpaidNoticePortalRequestDto()
//        val unpaidNoticeRequest = UnpaidNoticeRequestDto(
//                unpaidNoticePortalRequest.noticeNumber,
//                unpaidNoticePortalRequest.notificationDate,
//                nui,
//                null,
//                unpaidNoticePortalRequest.dueDate
//        )
//
//        var listUnPaidNotice = listUnpaidNoticeService.listUnpaidNotice(unpaidNoticeRequest)
//        listUnPaidNotice?.forEach { item ->
//            item.notificationDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.notificationDate)
//            item.dueDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.dueDate)
//        }
//        logger.trace {"Unpaid Notice List " +listUnPaidNotice }
//
//        val modelAndView = ModelAndView()
//        modelAndView.addObject("username", auth.name)
//        modelAndView.addObject("UnpaidNotice", listUnPaidNotice?: emptyList<UnpaidNoticePortalResponseDto>())
//        modelAndView.addObject("parentMenuHighlight", "notices-index")
//        modelAndView.addObject("menuHighlight", "notices-unpaid")
//        modelAndView.viewName = "portal/list-unpaid-customs"
//        return modelAndView
//    }

}