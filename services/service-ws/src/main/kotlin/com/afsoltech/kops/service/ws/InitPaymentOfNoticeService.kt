package com.afsoltech.kops.service.ws

import com.afsoltech.core.entity.temp.TempPayment
import com.afsoltech.core.entity.user.UserApp
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.exception.UnauthorizedException
import com.afsoltech.core.repository.temp.TempPaymentRepository
import com.afsoltech.core.repository.user.AccountBankRepository
import com.afsoltech.core.repository.user.UserAppRepository
import com.afsoltech.core.service.OTPService
import com.afsoltech.core.service.utils.StringDateFormaterUtils
import com.afsoltech.kops.core.model.InitPaymentRequestDto
import com.afsoltech.kops.core.model.InitPaymentResponseDto
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.core.repository.temp.SelectedNoticeBeneficiaryRepository
import com.afsoltech.kops.core.repository.temp.SelectedNoticeRepository
import com.afsoltech.kops.service.mapper.NoticeModelToEntity
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import mu.KLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.text.StringBuilder

@Service
class InitPaymentOfNoticeService(val selectedNoticeRepository: SelectedNoticeRepository, val accountBankRepository: AccountBankRepository,
                         val userAppRepository: UserAppRepository, val calculateFeeNoticeService: CalculateFeeNoticeService,
                                 val tempPaymentRepository: TempPaymentRepository) {

    companion object : KLogging(){
        var paymentIdInc : Long = 0L
    }

//    @Value("\${apiexternal..bank.epayment.askBankAuthPaymentUrl}")
//    lateinit var initBillPaymentUrl: String

    @Value("\${app.bank.code.initial}")
    lateinit var operatorCode: String

    @Value("\${app.bank.operation.code}")
    lateinit var operationCode: String

    @Value("\${app.bank.payment.mode}")
    lateinit var paymentMode: String

    @Value("\${app.provider.notice.code}")
    lateinit var providerNoticeCode: String

    init{
        paymentIdInc = tempPaymentRepository.count()
    }

    /**
     * To request to check user parameter and create temp payment record in db before ask authorization to the bank
     */
    @Transactional
    @Synchronized
    fun initPaymentOfNotice(user: UserApp, initPaymentRequest: InitPaymentRequestDto) : TempPayment { //:Boolean

        val account = accountBankRepository.findByAccountNumber(initPaymentRequest.acntNo)
            account?.let { account ->
            if(account.userApp!!.id!= user.id)
                throw BadRequestException("Kops.Error.Payment.User.Account", listOf(user.login!!, initPaymentRequest.acntNo))
            val selectedNoticeList = selectedNoticeRepository.findByUserLogin(user.login!!)

            var noticeAmount = BigDecimal.ZERO
            var externalAmount = BigDecimal.ZERO
            var customerNumber :String?=null
//            val noticeNumberList = mutableListOf<String>()
            val noticeNumberList = initPaymentRequest.noticeNumberList
            val noticeStr =StringBuilder()

            selectedNoticeList.forEach {notice ->
                noticeAmount += notice.amount!!
                notice.beneficiaryList.forEach { benef ->
                    externalAmount += if(benef.accountNumber!!.startsWith(operatorCode)) BigDecimal.ZERO else benef.amount!!
                }
                customerNumber?.let{
                    customerNumber = notice.taxpayerNumber
                }
                noticeStr.append(",").append(notice.noticeNumber)
//                noticeNumberList.add(notice.noticeNumber!!)
                if(!noticeNumberList.isNullOrEmpty() && !noticeNumberList.contains(notice.noticeNumber)){
                    throw BadRequestException("Kops.Error.Payment.Notice.Bad.List")
                }

            }

            if(noticeAmount.minus(initPaymentRequest.amount).toInt() != 0){
                throw BadRequestException("Kops.Error.Payment.Notice.Bad.Amount")
            }

                /*Evaluate fee Amount*/
            val feeDto = calculateFeeNoticeService.calculateFee(noticeAmount, externalAmount) ?:
                        throw BadRequestException("Kops.Error.Payment.Bank.FeeAmount.Null")
            val feeAmount = feeDto.feeAmount
            val totalAmount = noticeAmount + feeAmount
            if(totalAmount.minus(initPaymentRequest.totalAmount).toInt() != 0)
                throw BadRequestException("Kops.Error.Payment.Notice.Bad.TotalAmount")

            var internalPaymentNumber = String()
            do {
                internalPaymentNumber = operatorCode+StringDateFormaterUtils.DateToString.format(LocalDate.now())+ ""+
                        StringUtils.leftPad(""+paymentIdInc, 6, "0")
                paymentIdInc++
                val tempP = tempPaymentRepository.findByInternalPaymentNumber(internalPaymentNumber)
            }while(tempP.isNotEmpty())


            var tempPayment = TempPayment(internalPaymentNumber = internalPaymentNumber, bankCode = account.bankCode, bankAgencyCode = account.accountAgency,
                    customerNumber = customerNumber, payerAccountNumber = account.accountNumber, payerAccountName = account.accountName,
                    paymentMode = paymentMode, amount = noticeAmount, feeAmount = feeAmount, totalAmount = totalAmount,
                    paymentDate = LocalDateTime.now(), providerCode = providerNoticeCode, billNumber = noticeStr.substring(1),
                    operationCode=operationCode, currency=account.currency)

            tempPayment = tempPaymentRepository.save(tempPayment)

            // Build Response, Generate Otp and send to the user
//            val responseDto = InitPaymentResponseDto(tempPaymentId=tempPayment.id!!, acntNo = initPaymentRequest.acntNo, amount = noticeAmount,
//                    fee = feeAmount, totalAmount = tempPayment.totalAmount!!, numberOfNotice = selectedNoticeList.size)

            return tempPayment
        }
        throw BadRequestException("Kops.Error.Payment.Account.NotFound", listOf(initPaymentRequest.acntNo))

    }

}