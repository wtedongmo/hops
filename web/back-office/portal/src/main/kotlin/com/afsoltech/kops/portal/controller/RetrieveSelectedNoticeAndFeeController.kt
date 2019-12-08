package com.nanobnk.epayment.core.web

import com.afsoltech.core.exception.RestException
import com.afsoltech.core.service.AccountBankService
import com.afsoltech.core.util.enforce
import com.afsoltech.kops.core.model.BillAndFeeResponses
import com.afsoltech.kops.core.model.NoticeResponses
import com.afsoltech.kops.core.model.UnpaidNoticeRequestDto
import com.afsoltech.kops.core.model.attribute.FieldsAttribute
import com.afsoltech.kops.core.model.integration.OutSelectedNoticeRequestDto
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import com.afsoltech.kops.service.integration.RetrieveSelectedUnpaidNoticeService
import com.afsoltech.kops.service.ws.CalculateFeeNoticeService

import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.MessageSource
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RestController
//@RequestMapping("\${api.internal.customs.epayment.retrieveSelectedAndFeeNotice}")
class RetrieveSelectedNoticeAndFeeController(val retrieveSelectedUnpaidNoticeService: RetrieveSelectedUnpaidNoticeService,
                                             val calculateFeeNoticeService: CalculateFeeNoticeService, val accountBankService: AccountBankService){
    companion object : KLogging()

    @Autowired
    @Qualifier(value = "errorMessageSource")
    lateinit var messageSource: MessageSource

    @GetMapping("/portal/retrieve-selected-customs/{taxpayerNumber}")
    fun retrieveSelectedNoticeAndFee(@RequestParam taxpayerNumber: String, request: HttpServletRequest): ModelAndView {

        if(taxpayerNumber.isNullOrBlank())
            return ModelAndView("redirect:/portal/list-unpaid-customs?error=true");

        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name
        val selectedRequest= OutSelectedNoticeRequestDto(taxpayerNumber)
        val result = retrieveSelectedUnpaidNoticeService.listSelectedUnpaidNotice(selectedRequest, username, request)
            logger.trace { result }

        val billFeeDto = calculateFeeNoticeService.calculateFeeNotice(username)
        val accountList = accountBankService.findByUser(username)

        val modelAndView = ModelAndView()
        modelAndView.addObject("username", auth.name)
        modelAndView.addObject("message", "kops.payment.choose.account")
        modelAndView.addObject("selectedNotices", result.resultData)
        modelAndView.addObject("billFeeNotice", billFeeDto)
        modelAndView.addObject("accountList", accountList)
        // menu highlight
        modelAndView.addObject("parentMenuHighlight", "notices-index")
        modelAndView.addObject("menuHighlight", "notices-list")
        modelAndView.viewName = "portal/selected-unpaid-notice"
        return modelAndView

    }
}