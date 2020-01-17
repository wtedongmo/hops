package com.afsoltech.hops.agent.controller

import com.afsoltech.core.service.cap.AccountBankService
import com.afsoltech.core.service.utils.StringDateFormaterUtils
import com.afsoltech.hops.core.model.BillPaymentNoticeModel
import com.afsoltech.hops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.hops.service.integration.ListUnpaidNoticeService
import com.afsoltech.hops.service.ws.CalculateFeeNoticeService
import com.afsoltech.hops.service.ws.SaveSelectedNoticeService
import javassist.NotFoundException
import mu.KLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RestController
//@RequestMapping()
class AgentSaveSelectedUnpaidNoticeController (val saveSelectedNoticeService: SaveSelectedNoticeService,
                                          val calculateFeeNoticeService: CalculateFeeNoticeService, val accountBankService: AccountBankService){
    companion object : KLogging()

    @PostMapping("/agent-banking/save-selected-customs")
    fun saveSelectedNotices(@RequestParam("selectedNotices") selectedNoticeNumberList: List<String>?,
                           request: HttpServletRequest): ModelAndView { //

        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name

        logger.info("\nUser: $username; Selected Notices: $selectedNoticeNumberList" )

        //Call Service to save selected Notices
        if(selectedNoticeNumberList.isNullOrEmpty()){
            return ModelAndView("redirect:/agent-banking/list-unpaid-customs?error=true");
        }

        try{
            val selectedNoticeList = saveSelectedNoticeService.saveSelectedNotices(username, selectedNoticeNumberList)
            val selectedNotices = mutableListOf<UnpaidNoticeResponseDto>()
            var taxpayerNumber=""
            selectedNoticeList.forEach {notice ->
                val noticeCache = ListUnpaidNoticeService.unpaidNoticeCache!!.get(notice.noticeNumber!!)
                noticeCache.notificationDate = StringDateFormaterUtils.StringDateToDateFormat.format(noticeCache.notificationDate)
                noticeCache.dueDate = StringDateFormaterUtils.StringDateToDateFormat.format(noticeCache.dueDate)
                selectedNotices.add(noticeCache)

                if(taxpayerNumber.isEmpty())
                    taxpayerNumber = notice.taxpayerNumber!!
            }

            val billFeeDto = calculateFeeNoticeService.calculateFee(selectedNoticeList)
            billFeeDto.number = selectedNoticeList.size
            val accountList = accountBankService.findByUser(username)

            val modelAndView = ModelAndView()
            modelAndView.addObject("username", auth.name)
            modelAndView.addObject("message", "app.payment.bill.choose.account")
            modelAndView.addObject("selectedBills", selectedNotices)
            modelAndView.addObject("billFee", billFeeDto)
            modelAndView.addObject("accountList", accountList)
            val billFeeAct = BillPaymentNoticeModel(otp = null, accountNumber = null, selectedBills = selectedNotices, billFee = billFeeDto,
                    taxpayerNumber = taxpayerNumber)
            request.session.setAttribute(username+"_billPayInfo", billFeeAct)

            modelAndView.addObject("BillPayment", billFeeAct)
            // menu highlight
            modelAndView.addObject("parentMenuHighlight", "notices-index")
            modelAndView.addObject("menuHighlight", "notices-list")
            modelAndView.viewName = "agent-banking/bill-select-account"
            return modelAndView
        }catch (ex: Exception){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/agent-banking/list-unpaid-customs?errorMessage=admin.system.error")
        }
    }


    @GetMapping("/agent-banking/display-selected-customs/{taxpayerNumber}")
    fun displaySelectedNoticeAndFee(@PathVariable taxpayerNumber: String,
                                    @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                                    request: HttpServletRequest): ModelAndView {

        if(taxpayerNumber.isBlank() || taxpayerNumber.isEmpty())
            return ModelAndView("redirect:/agent-banking/list-unpaid-customs?error=true");


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
            modelAndView.viewName = "agent-banking/bill-select-account"
            return modelAndView
        }catch (ex: Exception){
            logger.error { ex.message+"\n"+ ex.printStackTrace()} //Error.Unable.to.resent.otp
            return ModelAndView("redirect:/agent-banking/list-unpaid-customs?errorMessage=" +ex.message);
        }


    }
}