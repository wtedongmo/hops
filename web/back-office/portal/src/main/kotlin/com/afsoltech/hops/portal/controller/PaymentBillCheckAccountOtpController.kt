package com.afsoltech.core.controller

import com.afsoltech.core.exception.NotFoundException
import com.afsoltech.core.exception.RestException
import com.afsoltech.core.model.attribute.RequestType
import com.afsoltech.core.service.user.OTPService
import com.afsoltech.core.service.utils.TranslateUtils
import com.afsoltech.hops.core.model.BillPaymentNoticeModel
import com.afsoltech.hops.service.ws.AccountBalanceService

import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RestController
class PaymentBillCheckAccountOtpController(val accountBalanceService: AccountBalanceService, val otpService: OTPService){
    companion object : KLogging()

//    @Autowired
//    @Qualifier(value = "errorMessageSource")
//    lateinit var messageSource: MessageSource

    @Autowired
    lateinit var translateUtils: TranslateUtils

    @PostMapping("/portal/payment-bill-check-account")
    fun paymentBillCheckAccountOtpValid(@ModelAttribute("BillPayment") billFeeAct: BillPaymentNoticeModel,
                                        @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                                        request: HttpServletRequest): ModelAndView {

        logger.trace { "Bill payment value $billFeeAct" }
        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name
        if(billFeeAct.accountNumber.isNullOrBlank()){
            return ModelAndView("redirect:/portal/display-selected-customs/${billFeeAct.taxpayerNumber}?errorMessage=account.number.not.found");
        }

        // Check Account balance
//        val accountBalance = accountBalanceService.getAccountBalance(billFeeAct.accountNumber!!, request)
//        if(accountBalance.balance!!.subtract(billFeeAct.billFee!!.totalAmount) < BigDecimal.ZERO){
//            return ModelAndView("redirect:/portal/display-selected-customs/${billFeeAct.taxpayerNumber}?errorMessage=" +
//                    "account.number.insufficient.amount");
//        }

        val billFeeActSession = request.session.getAttribute(username+"_billPayInfo") as BillPaymentNoticeModel
        logger.trace { "Bill payment session value $billFeeActSession" }
        try {
            // Generate and send OTP by mail
            otpService.generateOTPAndSendMail(username, RequestType.PAYMENT_OF_BILL,false)

            billFeeActSession.accountNumber = billFeeAct.accountNumber
            request.session.setAttribute(username+"_billPayInfo", billFeeActSession)

            //Build model and view for response
            val modelAndView = ModelAndView()
            modelAndView.addObject("username", auth.name)
            modelAndView.addObject("message", "app.payment.bill.otp.type")

            errorMessage?.let {
                modelAndView.addObject("errorMessage", errorMessage)
            }
            modelAndView.addObject("BillPayment", billFeeActSession)
            modelAndView.addObject("billFee", billFeeActSession.billFee)
            // menu highlight
            modelAndView.addObject("parentMenuHighlight", "notices-index")
            modelAndView.addObject("menuHighlight", "notices-list")
            modelAndView.viewName = "portal/bill-resume-otp"
            return modelAndView
        }catch (ex: RestException){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/portal/display-selected-customs/${billFeeActSession.taxpayerNumber}?errorMessage="+translateUtils.translate(ex.message?:""))
        }catch (ex: Exception){
            logger.error { ex.message+"\n"+ ex.printStackTrace()}
            return ModelAndView("redirect:/portal/display-selected-customs/${billFeeActSession.taxpayerNumber}?errorMessage=admin.system.error");
        }
    }

    @GetMapping("/portal/payment-bill-resend-otp/{taxpayerNumber}")
    fun resendOtpBillPayment(@PathVariable(value="taxpayerNumber") taxpayerNumber: String,
                             @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                             request: HttpServletRequest): ModelAndView{

        return formToTypeOtp(taxpayerNumber, true, request)
    }

    @GetMapping("/portal/payment-bill-bad-otp/{taxpayerNumber}")
    fun invalidOtp(@PathVariable(value="taxpayerNumber") taxpayerNumber: String,
                   @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                   request: HttpServletRequest): ModelAndView{

        val mav =  formToTypeOtp(taxpayerNumber,false, request)
        if(errorMessage.isNullOrBlank())
            mav.addObject("errorMessage", "otp.bad.value.provided")
        else
            mav.addObject("errorMessage", errorMessage)
        return mav
    }

    private fun formToTypeOtp(taxpayerNumber: String, resendOtp: Boolean, request: HttpServletRequest):ModelAndView{

        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name

        try {
            val billFeeAct = request.session.getAttribute(username+"_billPayInfo") as BillPaymentNoticeModel?:
                throw NotFoundException("Error.Session.Variable.Bill.Payment.NotFound")

            // Generate and send OTP by mail
            if(resendOtp)
                otpService.generateOTPAndSendMail(username, RequestType.PAYMENT_OF_BILL,true)

            val modelAndView = ModelAndView()
            modelAndView.addObject("username", auth.name)
            modelAndView.addObject("message", "app.payment.bill.otp.type")

            modelAndView.addObject("BillPayment", billFeeAct)
            modelAndView.addObject("billFee", billFeeAct.billFee)
            // menu highlight
            modelAndView.addObject("parentMenuHighlight", "notices-index")
            modelAndView.addObject("menuHighlight", "notices-list")
            modelAndView.viewName = "portal/bill-resume-otp"
            return modelAndView
        }catch (ex: Exception){
            logger.error { ex.message+"\n"+ ex.printStackTrace()} //Error.Unable.to.resent.otp
            return ModelAndView("redirect:/portal/display-selected-customs/${taxpayerNumber}?errorMessage=" +ex.message);
        }
    }
}