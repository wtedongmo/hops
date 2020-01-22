package com.afsoltech.hops.service.ws

import com.afsoltech.core.entity.cap.temp.TempPayment
import com.afsoltech.core.entity.user.UserApp
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.repository.cap.AccountBankRepository
import com.afsoltech.core.repository.cap.temp.TempPaymentRepository
import com.afsoltech.core.repository.user.UserAppRepository
import com.afsoltech.core.service.utils.LoadSettingDataToMap
import com.afsoltech.core.service.utils.StringDateFormatterUtils
import com.afsoltech.hops.core.entity.customs.temp.SelectedNotice
import com.afsoltech.hops.core.model.InitPaymentRequestDto
import com.afsoltech.hops.core.repository.temp.SelectedNoticeRepository
import mu.KLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.Instant
import java.time.ZoneId
import kotlin.text.StringBuilder

@Service
class InitPaymentOfNoticeService(val selectedNoticeRepository: SelectedNoticeRepository, val accountBankRepository: AccountBankRepository,
                         val userAppRepository: UserAppRepository, val calculateFeeNoticeService: CalculateFeeNoticeService,
                                 val tempPaymentRepository: TempPaymentRepository) {

    companion object : KLogging(){
        private var paymentIdInc : Long = 0L
    }

//    @Value("\${apiexternal..bank.epayment.askBankAuthPaymentUrl}")
//    lateinit var initBillPaymentUrl: String

//    @Value("\${app.bank.code.initial}")
//    private var bankCode: String=""

//    @Value("\${app.bank.operation.code}")
//    private var operationCode: String=""

//    @Value("\${app.bank.payment.mode}")
//    private var paymentMode: String=""

//    @Value("\${app.provider.notice.code}")
//    private var providerNoticeCode: String=""

    init{

//        bankCode = LoadBaseDataToMap.settingMap.get("app.bank.code.initial")?.value?: ""
//        operationCode = LoadBaseDataToMap.settingMap.get("app.bank.operation.code")?.value?: ""
//        paymentMode = LoadBaseDataToMap.settingMap.get("app.bank.payment.mode")?.value?: ""
//        providerNoticeCode = LoadBaseDataToMap.settingMap.get("app.provider.notice.code")?.value?: ""
        paymentIdInc = tempPaymentRepository.count()
    }


    fun initPaymentOfNotice(user: UserApp, initPaymentRequest: InitPaymentRequestDto) : TempPayment { //:Boolean

        val selectedNoticeList = selectedNoticeRepository.findByUserLogin(user.login!!)
        return initPaymentOfNotice(user, selectedNoticeList, initPaymentRequest)
    }
    /**
     * To request to check user parameter and create temp payment record in db before ask authorization to the bank
     */
    @Transactional
    @Synchronized
    fun initPaymentOfNotice(user: UserApp, selectedNoticeList: List<SelectedNotice>, initPaymentRequest: InitPaymentRequestDto) : TempPayment { //:Boolean

        val accountOp = accountBankRepository.findOneByAccountNo(initPaymentRequest.acntNo)
        if(accountOp.isPresent){
            val account = accountOp.get()
            if(account.userApp!!.id!= user.id)
                throw BadRequestException("Error.Payment.User.Account.NotMatch", listOf(user.login!!, initPaymentRequest.acntNo))

            var noticeAmount = BigDecimal.ZERO
            var externalAmount = BigDecimal.ZERO
//            var customerNumber :String?=null
            val selectedNoticeNumberList = mutableListOf<String>()
            val noticeNumberList = initPaymentRequest.noticeNumberList!!
            val noticeStr =StringBuilder()
            var nberExist=0

            selectedNoticeList.forEach {notice ->

//                noticeNumberList.add(notice.noticeNumber!!)
//                if(!noticeNumberList.isNullOrEmpty() && !noticeNumberList.contains(notice.noticeNumber)){
//                    throw BadRequestException("Error.Payment.Notice.Bad.List")
//                }
                selectedNoticeNumberList.add(notice.noticeNumber!!)
                if (noticeNumberList.contains(notice.noticeNumber!!)){
                    nberExist++
                    noticeAmount += notice.amount!!
                    notice.beneficiaryList.forEach { benef ->
                        externalAmount += if(benef.accountNumber!!.startsWith(LoadSettingDataToMap.bankCode)) BigDecimal.ZERO else benef.amount!!
                    }
                    noticeStr.append(",").append(notice.noticeNumber)
                }
            }

            if(!selectedNoticeNumberList.containsAll(noticeNumberList) || nberExist!= noticeNumberList.size){
                throw BadRequestException("Error.Payment.Notice.Bad.List")
            }

            if(noticeAmount.minus(initPaymentRequest.amount).toInt() != 0){
                throw BadRequestException("Error.Payment.Notice.Bad.Amount")
            }

                /*Evaluate fee Amount*/
            val feeDto = calculateFeeNoticeService.calculateFee(noticeAmount, externalAmount) ?:
                        throw BadRequestException("Error.Payment.Bank.FeeAmount.Null")
            val feeAmount = feeDto.feeAmount
            val totalAmount = noticeAmount + feeAmount
            if(totalAmount.minus(initPaymentRequest.totalAmount).toInt() != 0)
                throw BadRequestException("Error.Payment.Notice.Bad.TotalAmount")

            val internalPaymentNumber = getInternalPaymentNumber()

            val tempPayment = TempPayment(internalPaymentNumber = internalPaymentNumber, bankCode = LoadSettingDataToMap.bankCode,
                    bankAgencyCode = account.branchCode, customerNumber = initPaymentRequest.customerNumber, payerAccountNumber = account.accountNo,
                    payerAccountName = account.accountName, paymentMode = LoadSettingDataToMap.paymentMode, amount = noticeAmount, feeAmount = feeAmount,
                    totalAmount = totalAmount,  paymentDate = Instant.now(), providerCode = LoadSettingDataToMap.providerNoticeCode,
                    billNumber = noticeStr.substring(1), operationCode=LoadSettingDataToMap.operationCode, currency=account.currency, userLogin = user.login)

            return tempPaymentRepository.save(tempPayment)

            // Build Response, Generate Otp and send to the user
//            val responseDto = InitPaymentResponseDto(tempPaymentId=tempPayment.id!!, acntNo = initPaymentRequest.acntNo, amount = noticeAmount,
//                    fee = feeAmount, totalAmount = tempPayment.totalAmount!!, numberOfNotice = selectedNoticeList.size)

//            return tempPayment
        }
        throw BadRequestException("Error.Payment.Account.NotFound", listOf(initPaymentRequest.acntNo))

    }

//    @Synchronized
    private fun getInternalPaymentNumber(): String{
        var internalPaymentNumber = String()
        do {
            paymentIdInc++
            internalPaymentNumber = LoadSettingDataToMap.bankCode+StringDateFormatterUtils.DateToString.format(
                    LocalDate.now(ZoneId.of(LoadSettingDataToMap.timeZoneId)))+ ""+
                    StringUtils.leftPad(""+paymentIdInc, 6, "0")
            val tempP = tempPaymentRepository.findByInternalPaymentNumber(internalPaymentNumber)
        }while(tempP.isNotEmpty())
        return internalPaymentNumber
    }

}