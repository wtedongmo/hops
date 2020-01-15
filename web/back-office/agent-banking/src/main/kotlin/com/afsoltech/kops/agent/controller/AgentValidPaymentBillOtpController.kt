package com.afsoltech.kops.agent.controller

import com.afsoltech.core.service.AccountBankService
import com.afsoltech.core.service.user.OTPService
import com.afsoltech.core.service.utils.LoadBaseDataToMap
import com.afsoltech.kops.core.model.BillPaymentNoticeModel
import com.afsoltech.kops.core.model.BillPaymentResumeDto
import com.afsoltech.kops.core.model.InitPaymentRequestDto
import com.afsoltech.kops.service.ws.AccountBalanceService
import com.afsoltech.kops.service.ws.KopsPaymentOfNoticeService

import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RestController
class AgentValidPaymentBillOtpController(val kopsPaymentOfNoticeService: KopsPaymentOfNoticeService, val otpService: OTPService,
                                         val accountBalanceService: AccountBalanceService, val accountBankService: AccountBankService){
    companion object : KLogging()

    @Autowired
    lateinit var translateUtils: TranslateUtils

    @PostMapping("/agent-banking/payment-bill-valid")
    fun paymentBillOtpValid(@ModelAttribute("BillPayment") billFeeAct: BillPaymentNoticeModel,
                            request: HttpServletRequest): ModelAndView {

        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name
        val billFeeActSession = request.session.getAttribute(username+"_billPayInfo") as BillPaymentNoticeModel

        try {

            // Check Account balance
//            val accountBalance = accountBalanceService.getAccountBalance(billFeeAct.accountNumber!!, request)
//            if(accountBalance.balance!!.subtract(billFeeAct.billFee!!.totalAmount) < BigDecimal.ZERO){
//                return ModelAndView("redirect:/agent-banking/display-selected-customs/${billFeeAct.taxpayerNumber}?errorMessage=" +
//                        "account.number.insufficient.amount");
//            }
            request.session.setAttribute(username+"_billPayInfo", billFeeAct)
            // Validate OTP
//            if(!otpService.validateOTP(username, RequestType.PAYMENT_OF_BILL, billFeeAct.otp!!.toInt())){
//                return ModelAndView("redirect:/agent-banking/payment-bill-bad-otp/${billFeeActSession.taxpayerNumber}?errorMessage=otp.bad.value.provided");
//            }

            //List of notice number
            val noticeNumberList = mutableListOf<String>()
            billFeeActSession.selectedBills!!.forEach {
                noticeNumberList.add(it.noticeNumber!!)
            }
            billFeeActSession.accountNumber = billFeeAct.accountNumber
            //DTO for payment
            val paymentRequestDto = InitPaymentRequestDto(billFeeAct.accountNumber!!, billFeeActSession.billFee!!.amount,
                    billFeeActSession.billFee!!.feeAmount,
                    billFeeActSession.billFee!!.totalAmount,  billFeeActSession.taxpayerNumber!!, noticeNumberList)
            //Call Service to do payment
            val paymentResp = kopsPaymentOfNoticeService.paymentOfNotice(username, paymentRequestDto, request)

            val account = accountBankService.findByAccountNumber(billFeeActSession.accountNumber!!)
            val payResume :BillPaymentResumeDto
            if(account==null)
                payResume = BillPaymentResumeDto()
            else
                payResume = BillPaymentResumeDto(bankName = LoadBaseDataToMap.bankName, bankCode = LoadBaseDataToMap.bankCode,
                        bankAgency = account.accountAgency, accountNumber = account.accountNumber,
                        accountName = account.accountName, transactionNumber = paymentResp.bankPaymentNumber,
                        camcisPaymentNumber = paymentResp.camcisPaymentNumber,
                        paymentDate =  StringDateFormaterUtils.ParsePaymentDate.formatDateTime(paymentResp.paymentDate!!))

            //Build model and view for response
            val modelAndView = ModelAndView()
            modelAndView.addObject("username", auth.name) //translateUtils
            val message = translateUtils.translate("app.payment.bill.terminate", listOf(paymentResp.bankPaymentNumber!!))
            //messageSource.getMessage("bill.payment.terminate", arrayOf(paymentResp.bankPaymentNumber), "", request.locale)
            modelAndView.addObject("message", message)

            modelAndView.addObject("PaymentResponse", paymentResp)
            modelAndView.addObject("BillPayment", billFeeActSession)
            modelAndView.addObject("billPayResume", payResume)
            modelAndView.addObject("selectedBills", billFeeActSession.selectedBills)
            modelAndView.addObject("billFee", billFeeActSession.billFee)

            // menu highlight
            modelAndView.addObject("parentMenuHighlight", "notices-index")
            modelAndView.addObject("menuHighlight", "notices-list")
            modelAndView.viewName = "agent-banking/bill-payment-receipt"
            return modelAndView
        }catch (ex: Exception){
            logger.error { ex.message+"\n"+ ex.printStackTrace()}
            return ModelAndView("redirect:/agent-banking/display-selected-customs/${billFeeActSession.taxpayerNumber}?errorMessage=" +
                    "${ex.message?:"Null Exception found"}");
        }

    }
}