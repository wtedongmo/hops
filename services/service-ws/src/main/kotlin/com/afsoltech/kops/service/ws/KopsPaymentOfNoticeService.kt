package com.afsoltech.kops.service.ws

import com.afsoltech.core.entity.temp.PäymentResultCode
import com.afsoltech.core.entity.temp.TempPayment
import com.afsoltech.core.entity.user.UserApp
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.model.attribute.PaymentStatus
import com.afsoltech.core.repository.temp.TempPaymentRepository
import com.afsoltech.core.repository.user.AccountBankRepository
import com.afsoltech.core.repository.user.UserAppRepository
import com.afsoltech.core.service.OTPService
import com.afsoltech.core.service.utils.StringDateFormaterUtils
import com.afsoltech.kops.core.model.AskBankAuthPaymentRequestDto
import com.afsoltech.kops.core.model.AskBankAuthPaymentResponseDto
import com.afsoltech.kops.core.model.InitPaymentRequestDto
import com.afsoltech.kops.core.model.InitPaymentResponseDto
import com.afsoltech.kops.core.model.integration.NoticeOfPaymentDto
import com.afsoltech.kops.core.model.integration.PaymentProcessRequestDto
import com.afsoltech.kops.core.model.integration.PaymentProcessResponseDto
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.core.repository.temp.SelectedNoticeBeneficiaryRepository
import com.afsoltech.kops.core.repository.temp.SelectedNoticeRepository
import com.afsoltech.kops.service.mapper.NoticeModelToEntity
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import com.afsoltech.kops.service.integration.PaymentOfSelectedNoticesService
import mu.KLogging
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.lang.StringBuilder
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class KopsPaymentOfNoticeService(val selectedNoticeRepository: SelectedNoticeRepository, val tempPaymentRepository: TempPaymentRepository,
                                 val paymentOfSelectedNoticesService: PaymentOfSelectedNoticesService) {

    companion object : KLogging()

    @Value("\${api.bank.epayment.askBankAuthPaymentUrl}")
    lateinit var initBillPaymentUrl: String

    @Value("\${app.bank.code.initial}")
    lateinit var operatorCode: String

    @Value("\${app.provider.notice.code}")
    lateinit var providerNoticeCode: String

    @Transactional
    @Synchronized
    fun paymentOfNotice(user: UserApp, tempPayment: TempPayment) : PaymentProcessResponseDto? { //:Boolean


        val selectedNoticeList = selectedNoticeRepository.findByUserLogin(user.login!!)
        val noticeListDto = mutableListOf<NoticeOfPaymentDto>()
        selectedNoticeList.forEach {
            val noticePay = NoticeOfPaymentDto(it.remoteNoticeId!!, it.noticeNumber!!, it.amount!!)
            noticeListDto.add(noticePay)
        }

        val txDate = StringDateFormaterUtils.DateTimeToString.format(tempPayment.paymentDate)!!
        val paymentRequest = PaymentProcessRequestDto(bankPaymentNumber = tempPayment.internalPaymentNumber!!, bankCode = tempPayment.bankCode!!,
                bankName = tempPayment.bankName, taxpayerNumber = tempPayment.customerNumber!!, totalAmount = tempPayment.amount!!, paymentDate = txDate,
                accountNumber = tempPayment.payerAccountNumber!!, accountName = tempPayment.payerAccountName, paymentMethod = tempPayment.paymentMode!!,
                noticeList = noticeListDto)

        var response : PaymentProcessResponseDto? = null
//        try {
            response = paymentOfSelectedNoticesService.paymentOfSelectedNotice(tempPayment, paymentRequest)
//        }catch (ex: Exception){
//            logger.error { "Exception:"+ex.message+ "\n"+ ex.printStackTrace()}
//            throw ex
//        }

        return response
    }

}