package com.afsoltech.core.controller

import com.afsoltech.core.service.cap.AccountBankService
import com.afsoltech.core.service.utils.StringDateFormaterUtils
import com.afsoltech.kops.core.model.BillPaymentNoticeModel
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import com.afsoltech.kops.service.ws.CalculateFeeNoticeService
import com.afsoltech.kops.service.ws.SaveSelectedNoticeService
import mu.KLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/portal/save-selected-customs")
class SaveSelectedUnpaidNoticeController (val saveSelectedNoticeService: SaveSelectedNoticeService,
                                          val calculateFeeNoticeService: CalculateFeeNoticeService, val accountBankService: AccountBankService){
    companion object : KLogging()

//    @Autowired
//    @Qualifier(value = "errorMessageSource")
//    lateinit var messageSource: MessageSource

    @PostMapping
    fun saveSelectedNotices(@RequestParam("selectedNotices") selectedNoticeNumberList: List<String>?,
                           request: HttpServletRequest): ModelAndView { //

        try{
            val auth = SecurityContextHolder.getContext().authentication
            val username= auth.name

            logger.info("\nUser: $username; Selected Notices: $selectedNoticeNumberList" )

            //Call Service to save selected Notices
            if(selectedNoticeNumberList.isNullOrEmpty()){
                return ModelAndView("redirect:/portal/list-unpaid-customs?error=true");
            }
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
            modelAndView.viewName = "portal/bill-select-account"
            return modelAndView
        }catch (ex: Exception){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/portal/list-unpaid-customs?errorMessage=admin.system.error")
        }
    }
}