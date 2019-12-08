package com.afsoltech.kops.service.ws

import com.afsoltech.core.entity.temp.PäymentResultCode
import com.afsoltech.core.entity.temp.TempPayment
import com.afsoltech.core.entity.user.UserApp
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.model.attribute.PaymentStatus
import com.afsoltech.core.repository.PaymentRepository
import com.afsoltech.core.repository.temp.TempPaymentRepository
import com.afsoltech.core.service.utils.LoadBaseDataToMap
import com.afsoltech.core.service.utils.StringDateFormaterUtils
import com.afsoltech.kops.core.entity.customs.NoticeBeneficiary
import com.afsoltech.kops.core.model.InitPaymentRequestDto
import com.afsoltech.kops.core.model.NoticeRequestDto
import com.afsoltech.kops.core.model.integration.NoticeOfPaymentDto
import com.afsoltech.kops.core.model.integration.PaymentProcessRequestDto
import com.afsoltech.kops.core.model.integration.PaymentProcessResponseDto
import com.afsoltech.kops.core.repository.NoticeBeneficiaryRepository
import com.afsoltech.kops.core.repository.NoticeRepository
import com.afsoltech.kops.core.repository.PaymentOfNoticeRepository
import com.afsoltech.kops.core.repository.temp.SelectedNoticeRepository
import com.afsoltech.kops.service.integration.DeleteTemporaryNoticeService
import com.afsoltech.kops.service.integration.ListPaidNoticeService
import com.afsoltech.kops.service.integration.PaymentOfSelectedNoticesService
import com.afsoltech.kops.service.mapper.NoticeModelToEntity
import com.nanobnk.epayment.service.mapper.NoticeEntityToEntity
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import javax.servlet.http.HttpServletRequest

@Service
class KopsPaymentOfNoticeService(val selectedNoticeRepository: SelectedNoticeRepository, val noticeRepository: NoticeRepository,
                                 val beneficiaryRepository: NoticeBeneficiaryRepository, val paymentOfNoticeRepository: PaymentOfNoticeRepository,
                                 val paymentRepository: PaymentRepository, val listPaidNoticeService: ListPaidNoticeService,
                                 val deleteTemporaryNoticeService: DeleteTemporaryNoticeService, val askBankCancelPaymentService: AskBankCancelPaymentService,
                                 val initPaymentOfNoticeService: InitPaymentOfNoticeService, val askBankAuthPaymentService: AskBankAuthPaymentService,
                                 val paymentOfSelectedNoticesService: PaymentOfSelectedNoticesService) {

    companion object : KLogging()

    @Value("\${app.bank.code.initial}")
    private lateinit var operatorCode: String

    @Value("\${app.notice.provider.code}")
    private lateinit var providerNoticeCode: String
    @Value("\${app.payment.notice.check.number:5}")
    private var checkNumberApp: Int=5

    @Transactional
    @Synchronized
    fun paymentOfNotice(user: UserApp, initPaymentRequest: InitPaymentRequestDto, request: HttpServletRequest) : PaymentProcessResponseDto { //:Boolean

        val tempPayment = initPaymentOfNoticeService.initPaymentOfNotice(user, initPaymentRequest)
        val askBankPaymentAuth = askBankAuthPaymentService.askBankAuthPayment(user, tempPayment)
        if(askBankPaymentAuth.resultCode.equals(PäymentResultCode.S.name)&&
                askBankPaymentAuth.resultData?.authRsltCd!!.equals(LoadBaseDataToMap.bankAuthCodeApproved!!)){
            var callApiCancel=false
            try {
                val selectedNoticeList = selectedNoticeRepository.findByUserLogin(user.login!!)
                val noticeListDto = mutableListOf<NoticeOfPaymentDto>()
                val noticeNumberToPayList = mutableSetOf<String>()
                selectedNoticeList.forEach {
                    val noticePay = NoticeOfPaymentDto(it.remoteNoticeId!!, it.noticeNumber!!, it.amount!!)
                    noticeListDto.add(noticePay)
                    noticeNumberToPayList.add(it.noticeNumber!!)
                }

                val txDate = StringDateFormaterUtils.DateTimeToString.format(tempPayment.paymentDate)!!
                val paymentRequest = PaymentProcessRequestDto(bankPaymentNumber = tempPayment.internalPaymentNumber!!, bankCode = tempPayment.bankCode!!,
                        bankName = tempPayment.bankName, taxpayerNumber = tempPayment.customerNumber!!, totalAmount = tempPayment.amount!!, paymentDate = txDate,
                        accountNumber = tempPayment.payerAccountNumber!!, accountName = tempPayment.payerAccountName, paymentMethod = tempPayment.paymentMode!!,
                        noticeList = noticeListDto)

                var response  = paymentOfSelectedNoticesService.paymentOfSelectedNotice(tempPayment, paymentRequest)

                val paidNoticeRequest = NoticeRequestDto(taxpayerNumber = tempPayment.customerNumber, paymentDate = LocalDate.now().toString())

                var continueCheck=true
                var noticePaidBool=false
                var checkNumber=0
                do {
                    // Call API of paid notice to check that all notices has been effectively paid from Camcis
                    val payNoticeList = listPaidNoticeService.listPaidNotice(paidNoticeRequest, null)
                    if(payNoticeList.resultCode.equals(PäymentResultCode.S.name)){
                        val noticeNumberList = mutableSetOf<String>()
                        val paymentNumberList = mutableSetOf<String>()
                        payNoticeList.resultData?.forEach { notice ->
                            if(noticeNumberToPayList.contains(notice.noticeNumber))
                                noticeNumberList.add(notice.noticeNumber!!)
                            paymentNumberList.add(notice.paymentNumber!!)
                        }
                        if(noticeNumberList.containsAll(noticeNumberToPayList)){
                            var paymentId: Long? = null
                            if (response.resultCode!!.equals(PäymentResultCode.S.name)) {
                                paymentId = createPaymentAndNoticeOfSuccessResponse(paymentRequest, tempPayment)
                                continueCheck= false
                                noticePaidBool=true
                            }
                        }else{
                            response.noticesList!!.forEach {
                                if(noticeNumberList.contains(it.noticeNumber) && !paymentNumberList.contains(response.bankPaymentNumber)){
                                    continueCheck= false
                                    noticePaidBool=false
                                }
                            }
                        }
                    }else if(!response.resultCode!!.equals(PäymentResultCode.S.name) || !response.paymentResultCode!!.equals("01")){
                        continueCheck= false
                        noticePaidBool=false
                    }else if(response.resultCode!!.equals(PäymentResultCode.S.name) && response.paymentResultCode!!.equals("01") && checkNumber==4){
                        val paymentId = createPaymentAndNoticeOfSuccessResponse(paymentRequest, tempPayment)
                        continueCheck= false
                        noticePaidBool=true
                    }
                    checkNumber++
                }while (continueCheck && checkNumber<checkNumberApp)

                // if result is not success, Call API to cancel reservation from bank
                if(!noticePaidBool ){
                    //Call API to Cancel
                    var retryNumber=0
                    do {
                        val cancelResp = askBankCancelPaymentService.askBankCancelPayment(user, tempPayment)
                        callApiCancel= true
                        retryNumber++
                    }while (retryNumber<5 && !cancelResp.resultCode.equals("S"))

                    if(response.resultCode!!.equals("S"))
                        throw BadRequestException("Kops.Error.Payment.User.NotFound", listOf(user.login!!))
                }

                return response
            } catch (ex: Exception){
                //Call API to cancel reservation from bank
                if(!callApiCancel){
                    var retryNumber=0
                    do {
                        val cancelResp = askBankCancelPaymentService.askBankCancelPayment(user, tempPayment)
                        callApiCancel= true
                        retryNumber++
                    }while (retryNumber<5 && !cancelResp.resultCode.equals("S"))
                }
                throw ex
            }
        }

        throw BadRequestException("Kops.Error.Payment.User.NotFound", listOf(user.login!!))
    }


    @Transactional
    @Synchronized
    fun createPaymentAndNoticeOfSuccessResponse(paymentRequest: PaymentProcessRequestDto, tempPayment: TempPayment): Long {

        //Create payment entity
        val paymentEntity = NoticeModelToEntity.PaymentProcessToEntity.from(paymentRequest, tempPayment)
        paymentEntity.paymentStatus = PaymentStatus.COMPLETED
        val paymentSaved = paymentRepository.save(paymentEntity)

        val noticePaidList = NoticeModelToEntity.PaymentOfNoticeModelToEntities.from(paymentRequest.noticeList)

        val noticeNumberList = mutableListOf<String>()
        noticePaidList.forEach { notice ->
            notice.payment = paymentSaved
            noticeNumberList.add(notice.noticeNumber!!)
        }

        val noticePaidListSaved = paymentOfNoticeRepository.saveAll(noticePaidList)

        val selectedNoticeList = selectedNoticeRepository.findListNoticeNumber(noticeNumberList)

        val noticesList = NoticeEntityToEntity.SelectedNoticeEntityToNoticeEntities.from(selectedNoticeList)
        noticesList.forEach { notice ->
            notice.noticeStatus = PaymentStatus.COMPLETED
            notice.paymentId = paymentSaved.id
            notice.paymentDate = paymentSaved.paymentDate
            notice.paymentMode = paymentSaved.paymentMode
            notice.paymentCategory = "004"
            notice.paymentAmount = notice.amount // noticePaidListSaved.find{ it -> it.selectedNoticeNumber.equals(customs.noticeNumber) }?.amount
            notice.paymentNumber = paymentSaved.paymentNumber
        }
        val noticeSavedList = noticeRepository.saveAll(noticesList)

        val noticeBenefList = mutableListOf<NoticeBeneficiary>()

        val outbNoticeMap = selectedNoticeList.map { notice -> notice.noticeNumber!! to notice }.toMap()

        noticeNumberList.clear()
        noticeSavedList.forEach { notice ->
            val listBenef = NoticeEntityToEntity.SelectedNoticeBeneficiaryEntityToEntities
                    .from(outbNoticeMap.get(notice.noticeNumber)?.beneficiaryList ?: emptyList())
            listBenef.forEach { benef ->
                benef.notice = notice
                noticeBenefList.add(benef)
            }

            noticeNumberList.add(notice.noticeNumber?:"")
        }

        val noticeBenefSaved = beneficiaryRepository.saveAll(noticeBenefList)

        // Delete checked customs list
        deleteTemporaryNoticeService.deleteSelectedNoticeAfterPayment(noticeNumberList)

        return paymentSaved.id!!
    }
}