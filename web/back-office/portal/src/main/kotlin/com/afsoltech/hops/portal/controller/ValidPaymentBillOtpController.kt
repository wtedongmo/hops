package com.afsoltech.core.controller

import com.afsoltech.core.exception.RestException
import com.afsoltech.core.model.attribute.RequestType
import com.afsoltech.core.service.cap.AccountBankService
import com.afsoltech.core.service.user.OTPService
import com.afsoltech.core.service.utils.LoadSettingDataToMap
import com.afsoltech.core.service.utils.StringDateFormatterUtils
import com.afsoltech.core.service.utils.TranslateUtils
import com.afsoltech.hops.core.model.BillPaymentNoticeModel
import com.afsoltech.hops.core.model.BillPaymentResumeDto
import com.afsoltech.hops.core.model.InitPaymentRequestDto
import com.afsoltech.hops.service.ws.HopsPaymentOfNoticeService

import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RestController
class ValidPaymentBillOtpController(val hopsPaymentOfNoticeService: HopsPaymentOfNoticeService, val otpService: OTPService,
                                    val accountBankService: AccountBankService){
    companion object : KLogging()

//    @Autowired
//    @Qualifier(value = "errorMessageSource")
//    lateinit var messageSource: MessageSource

    @Autowired
    lateinit var translateUtils: TranslateUtils

    @PostMapping("/portal/payment-bill-valid-otp")
    fun paymentBillOtpValid(@ModelAttribute("BillPayment") billFeeAct: BillPaymentNoticeModel,
                            request: HttpServletRequest): ModelAndView {

        val auth = SecurityContextHolder.getContext().authentication
        val username= auth.name
        if(billFeeAct.otp.isNullOrBlank()){
            return ModelAndView("redirect:/portal/payment-bill-bad-otp/${billFeeAct.taxpayerNumber}?errorMessage=otp.not.found");
        }

        val billFeeActSession = request.session.getAttribute(username+"_billPayInfo") as BillPaymentNoticeModel

        try {

            request.session.setAttribute(username+"_billPayInfo", billFeeAct)
            // Validate OTP
            if(!otpService.validateOTP(username, RequestType.PAYMENT_OF_BILL, billFeeAct.otp!!.toInt())){
                return ModelAndView("redirect:/portal/payment-bill-bad-otp/${billFeeActSession.taxpayerNumber}?errorMessage=otp.bad.value.provided");
            }

            //List of notice number
            val noticeNumberList = mutableListOf<String>()
            billFeeActSession.selectedBills!!.forEach {
                noticeNumberList.add(it.noticeNumber!!)
            }
            //DTO for payment
            val paymentRequestDto = InitPaymentRequestDto(billFeeActSession.accountNumber!!, billFeeActSession.billFee!!.amount,
                    billFeeActSession.billFee!!.feeAmount,
                    billFeeActSession.billFee!!.totalAmount,  billFeeActSession.taxpayerNumber!!, noticeNumberList)
            //Call Services to do payment
            val paymentResp = hopsPaymentOfNoticeService.paymentOfNotice(username, paymentRequestDto, request)

            val account = accountBankService.findByAccountNumber(billFeeActSession.accountNumber!!)
            val payResume :BillPaymentResumeDto
            if(account==null)
                payResume = BillPaymentResumeDto()
            else
                payResume = BillPaymentResumeDto(bankName = LoadSettingDataToMap.bankName, bankCode = LoadSettingDataToMap.bankCode,
                        bankAgency = account.accountAgency, accountNumber = account.accountNumber,
                        accountName = account.accountName, transactionNumber = paymentResp.bankPaymentNumber,
                        camcisPaymentNumber = paymentResp.camcisPaymentNumber,
                        paymentDate =  StringDateFormatterUtils.ParsePaymentDate.formatDateTime(paymentResp.paymentDate!!, LoadSettingDataToMap.timeZoneId))

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
            modelAndView.viewName = "portal/bill-payment-receipt"
            return modelAndView
        }catch (ex: RestException){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/portal/list-unpaid-customs?errorMessage="+(ex.message?:""))
        }catch (ex: Exception){
            logger.error { ex.message+"\n"+ ex.printStackTrace()}
            return ModelAndView("redirect:/portal/payment-bill-bad-otp/${billFeeActSession.taxpayerNumber}?errorMessage=" +
                    "admin.system.error");
        }

    }
}