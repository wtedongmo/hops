package com.nanobnk.epayment.core.web

import com.afsoltech.core.exception.RestException
import com.afsoltech.core.service.AccountBankService
import com.afsoltech.core.util.enforce
import com.afsoltech.kops.core.model.NoticeResponses
import com.afsoltech.kops.core.model.attribute.FieldsAttribute
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.portal.controller.ListUnpaidNoticeController
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import com.afsoltech.kops.service.ws.CalculateFeeNoticeService
import com.afsoltech.kops.service.ws.SaveSelectedNoticeService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.MessageSource
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/portal/save-selected-customs")
class SaveSelectedUnpaidNoticeController (val saveSelectedNoticeService: SaveSelectedNoticeService,
                                          val calculateFeeNoticeService: CalculateFeeNoticeService, val accountBankService: AccountBankService){
    companion object : KLogging()

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource

    @PostMapping
    fun saveSelectedNotices(@RequestParam("selectedNotices") selectedNoticeNumberList: List<String>?,
                           request: HttpServletRequest): ModelAndView { //

        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name

        ListUnpaidNoticeController.logger.info("\nUser: $username; Selected Notices: $selectedNoticeNumberList" )

        //Call Service to save selected Notices
        if(selectedNoticeNumberList.isNullOrEmpty()){
            return ModelAndView("redirect:/portal/list-unpaid-customs?error=true");
        }
        saveSelectedNoticeService.saveSelectedNotices(username, selectedNoticeNumberList)
        val selectedNotices = mutableListOf<UnpaidNoticeResponseDto>()
        selectedNoticeNumberList.forEach {
            selectedNotices.add(ListUnpaidNoticeService.unpaidNoticeCache!!.get(it))
        }

        val billFeeDto = calculateFeeNoticeService.calculateFeeNotice(username)
        val accountList = accountBankService.findByUser(username)

        val modelAndView = ModelAndView()
        modelAndView.addObject("username", auth.name)
        modelAndView.addObject("message", "kops.payment.choose.account")
        modelAndView.addObject("selectedNotices", selectedNotices)
        modelAndView.addObject("billFeeNotice", billFeeDto)
        modelAndView.addObject("accountList", accountList)
        // menu highlight
        modelAndView.addObject("parentMenuHighlight", "notices-index")
        modelAndView.addObject("menuHighlight", "notices-list")
        modelAndView.viewName = "portal/selected-unpaid-notice"
        return modelAndView
    }
}