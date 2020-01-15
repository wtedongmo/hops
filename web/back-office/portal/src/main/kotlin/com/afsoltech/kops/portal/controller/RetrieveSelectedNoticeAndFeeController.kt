package com.afsoltech.core.controller

import com.afsoltech.core.exception.NotFoundException
import com.afsoltech.core.service.AccountBankService
import com.afsoltech.kops.core.model.BillPaymentNoticeModel
import com.afsoltech.kops.core.model.integration.OutSelectedNoticeRequestDto
import com.afsoltech.kops.service.integration.RetrieveSelectedUnpaidNoticeService
import com.afsoltech.kops.service.ws.CalculateFeeNoticeService

import mu.KLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.lang.Exception
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/portal")
class RetrieveSelectedNoticeAndFeeController(val retrieveSelectedUnpaidNoticeService: RetrieveSelectedUnpaidNoticeService,
                                             val calculateFeeNoticeService: CalculateFeeNoticeService, val accountBankService: AccountBankService){
    companion object : KLogging()


    @GetMapping("/retrieve-selected-customs/{taxpayerNumber}")///
    fun retrieveSelectedNoticeAndFee(@PathVariable taxpayerNumber: String, //(value = "taxpayerNumber", required = false)
                                     @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                                     request: HttpServletRequest): ModelAndView {

//        if(taxpayerNumber.isNullOrBlank())
//            return ModelAndView("redirect:/portal/list-unpaid-customs?error=true");

        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name
        val selectedRequest= OutSelectedNoticeRequestDto(taxpayerNumber)
        val result = retrieveSelectedUnpaidNoticeService.listSelectedUnpaidNotice(selectedRequest, username, request)
            logger.trace { "Retrieved selected unpaid notice $result" }

//        var billFeeDto :BillFeeDto?=null
        val selectedNoticeList = result.result()
        val noticeNumberList = mutableListOf<String>()
        selectedNoticeList.forEach { item ->
            item.notificationDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.notificationDate)
            item.dueDate = StringDateFormaterUtils.StringDateToDateFormat.format(item.dueDate)
            noticeNumberList.add(item.noticeNumber!!)
        }
        val billFeeDto = calculateFeeNoticeService.calculateFeeNotice(noticeNumberList)
        billFeeDto.number = selectedNoticeList.size

        val accountList = accountBankService.findByUser(username)

        val modelAndView = ModelAndView()
        errorMessage?.let {
            modelAndView.addObject("errorMessage", errorMessage)
        }

        modelAndView.addObject("username", auth.name)
        modelAndView.addObject("message", "app.payment.bill.choose.account")
        modelAndView.addObject("selectedBills", selectedNoticeList)
        modelAndView.addObject("billFee", billFeeDto)
        modelAndView.addObject("accountList", accountList)

        val billFeeAct = BillPaymentNoticeModel(otp = null, accountNumber = null, selectedBills = selectedNoticeList, billFee = billFeeDto,
                taxpayerNumber = taxpayerNumber)
        modelAndView.addObject("BillPayment", billFeeAct)
        request.session.setAttribute(username+"_billPayInfo", billFeeAct)
        // menu highlight
        modelAndView.addObject("parentMenuHighlight", "notices-index")
        modelAndView.addObject("menuHighlight", "notices-list")
        modelAndView.viewName = "portal/bill-select-account"
        return modelAndView

    }

    /*Portal form to type NIU Customs Potal User*/
    @GetMapping("/retrieve-selected-customer-form")
    fun formSelectedNotice(@RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                           request: HttpServletRequest): ModelAndView {
        val modelAndView = ModelAndView()
        errorMessage?.let {
            modelAndView.addObject("errorMessage", errorMessage)
        }
        modelAndView.addObject("niu", String())
        modelAndView.addObject("parentMenuHighlight", "notices-index")
        modelAndView.addObject("menuHighlight", "notices-selected")
        modelAndView.viewName = "portal/customer-selected-notice-form"
        return modelAndView
    }

    @PostMapping("/retrieve-selected-customs")
    fun retrieveSelectedNotice(@ModelAttribute("Customs") customerNiu: String,
                               request: HttpServletRequest): ModelAndView {
        return ModelAndView("redirect:/portal/retrieve-selected-customs/$customerNiu")
    }

    @GetMapping("/display-selected-customs/{taxpayerNumber}")
    fun displaySelectedNoticeAndFee(@PathVariable taxpayerNumber: String,
                                     @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                                     request: HttpServletRequest): ModelAndView {

        if(taxpayerNumber.isBlank() || taxpayerNumber.isEmpty())
            return ModelAndView("redirect:/portal/list-unpaid-customs?error=true");


        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name

        try {
            val billFeeAct = request.session.getAttribute(username+"_billPayInfo") as BillPaymentNoticeModel?:
                throw NotFoundException("Error.Session.Variable.Bill.Payment.NotFound")

            val accountList = accountBankService.findByUser(username)

            val modelAndView = ModelAndView()

            errorMessage?.let {
                modelAndView.addObject("errorMessage", errorMessage)
            }

            modelAndView.addObject("username", auth.name)
            modelAndView.addObject("message", "app.payment.bill.choose.account")
            modelAndView.addObject("selectedBills", billFeeAct.selectedBills)
            modelAndView.addObject("billFeeNotice", billFeeAct.billFee)
            modelAndView.addObject("accountList", accountList)

            modelAndView.addObject("BillPayment", billFeeAct)
            request.session.setAttribute(username+"_billPayInfo", billFeeAct)
            // menu highlight
            modelAndView.addObject("parentMenuHighlight", "notices-index")
            modelAndView.addObject("menuHighlight", "notices-list")
            modelAndView.viewName = "portal/bill-select-account"
            return modelAndView
        }catch (ex: Exception){
            logger.error { ex.message+"\n"+ ex.printStackTrace()} //Error.Unable.to.resent.otp
            return ModelAndView("redirect:/portal/retrieve-selected-customs/${taxpayerNumber}?errorMessage=" +ex.message);
        }


    }
}