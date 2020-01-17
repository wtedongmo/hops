package com.afsoltech.kops.service.ws

import com.afsoltech.core.entity.cap.temp.PäymentResultCode
import com.afsoltech.core.entity.cap.temp.TempPayment
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.model.attribute.PaymentStatus
import com.afsoltech.core.repository.cap.PaymentRepository
import com.afsoltech.core.repository.user.UserAppRepository
import com.afsoltech.core.service.utils.LoadSettingDataToMap
import com.afsoltech.core.service.utils.StringDateFormaterUtils
import com.afsoltech.kops.core.entity.customs.NoticeBeneficiary
import com.afsoltech.kops.core.model.InitPaymentRequestDto
import com.afsoltech.kops.core.model.notice.NoticeRequestDto
import com.afsoltech.kops.core.model.integration.NoticeOfPaymentDto
import com.afsoltech.kops.core.model.integration.PaymentProcessRequestDto
import com.afsoltech.kops.core.model.integration.PaymentProcessResponseDto
import com.afsoltech.kops.core.repository.NoticeBeneficiaryRepository
import com.afsoltech.kops.core.repository.NoticeRepository
import com.afsoltech.kops.core.repository.PaymentOfNoticeRepository
import com.afsoltech.kops.core.repository.temp.SelectedNoticeRepository
import com.afsoltech.kops.service.integration.ListPaidNoticeService
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import com.afsoltech.kops.service.integration.PaymentOfSelectedNoticesService
import com.afsoltech.kops.service.mapper.NoticeModelToEntity
import com.afsoltech.epayment.service.mapper.NoticeEntityToEntity
import com.afsoltech.kops.core.entity.customs.temp.SelectedNotice
import com.afsoltech.kops.core.model.AskBankAuthPaymentRespDataDto
import com.afsoltech.kops.core.model.AskBankAuthPaymentResponseDto
import mu.KLogging
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.*
import javax.servlet.http.HttpServletRequest

@Service
class KopsPaymentOfNoticeService(val userAppRepository: UserAppRepository,
                                 val selectedNoticeRepository: SelectedNoticeRepository, val noticeRepository: NoticeRepository,
                                 val beneficiaryRepository: NoticeBeneficiaryRepository, val paymentOfNoticeRepository: PaymentOfNoticeRepository,
                                 val paymentRepository: PaymentRepository, val listPaidNoticeService: ListPaidNoticeService,
                                 val deleteTemporaryNoticeService: DeleteTemporaryNoticeService, val askBankCancelPaymentService: AskBankCancelPaymentService,
                                 val initPaymentOfNoticeService: InitPaymentOfNoticeService, val askBankAuthPaymentService: AskBankAuthPaymentService,
                                 val paymentOfSelectedNoticesService: PaymentOfSelectedNoticesService) {

    companion object : KLogging()

//    @Value("\${app.payment.notice.check.number:5}")
    private var checkNumberApp: Int=5
    private var sleepMillis: Long=3000

    //    @Value("\${app.bank.code.initial}")
//    private var bankCode: String=""
    //    @Value("\${app.provider.notice.code}")
//    private var providerNoticeCode: String=""

//    init{
//        bankCode = LoadBaseDataToMap.settingMap.get("app.bank.code.initial")?.value?: ""
//        providerNoticeCode = LoadBaseDataToMap.settingMap.get("app.provider.notice.code")?.value?: ""
//    }


//    @Transactional
//    @Synchronized
    fun paymentOfNotice(userLogin: String, initPaymentRequest: InitPaymentRequestDto, request: HttpServletRequest) : PaymentProcessResponseDto { //:Boolean

        val userAppOp = userAppRepository.findOneByUsername(userLogin)
        if(userAppOp.isPresent) {
            val user = userAppOp.get()
            val selectedNoticeList = selectedNoticeRepository.findByUserLogin(user.login!!)
            val tempPayment = initPaymentOfNoticeService.initPaymentOfNotice(user, selectedNoticeList, initPaymentRequest)
            logger.trace { "Saved init Payment: $tempPayment" }
//            val askBankPaymentAuth = askBankAuthPaymentService.askBankAuthPayment(user, tempPayment)

            val random = Random()
            val authCode = 10000000 + random.nextInt(90000000)
            val txDate = StringDateFormaterUtils.DateTimeToString.format(tempPayment.paymentDate)
            val askBankPaymentAuth = AskBankAuthPaymentResponseDto(PäymentResultCode.S.name, "Success",
                    AskBankAuthPaymentRespDataDto(opCode=tempPayment.operationCode!!, acntNo = tempPayment.payerAccountNumber!!,
                    providerCode = tempPayment.providerCode!!, customerNo = tempPayment.customerNumber!!, trxRefNo = tempPayment.internalPaymentNumber!!,
                    trxDt = txDate!!, amount = tempPayment.amount!!, fee = tempPayment.feeAmount!!, totalAmount = tempPayment.totalAmount!!,
                    currency = tempPayment.currency!!, billNumberList = initPaymentRequest.noticeNumberList,
                            authCd=authCode.toString(), authRsltCd="001", authRsltMsg="Success"))


            if(askBankPaymentAuth.resultCode.equals(PäymentResultCode.S.name)&&
                    askBankPaymentAuth.resultData?.authRsltCd!!.equals(LoadSettingDataToMap.bankAuthCodeApproved)){
                var callApiCancel=false
                try {
                    val noticeListDto = mutableListOf<NoticeOfPaymentDto>()
                    val noticeNumberToPayList = initPaymentRequest.noticeNumberList!!
                    selectedNoticeList.forEach {
                        if(noticeNumberToPayList.contains(it.noticeNumber)){
                            val noticePay = NoticeOfPaymentDto(it.remoteNoticeId!!, it.noticeNumber!!, it.amount!!)
                            noticeListDto.add(noticePay)
                        }
//                        noticeNumberToPayList.add(it.noticeNumber!!)
                    }

                    val selectedNoticeToPaidList = selectedNoticeRepository.findListNoticeNumber(noticeNumberToPayList)

                    tempPayment.bankAuthNumber = askBankPaymentAuth.resultData?.authCd

                    val txDate = StringDateFormaterUtils.DateTimeToString.format(tempPayment.paymentDate)!!
                    val paymentRequest = PaymentProcessRequestDto(bankPaymentNumber = tempPayment.internalPaymentNumber!!, bankCode = tempPayment.bankCode!!,
                            bankName = tempPayment.bankName, taxpayerNumber = tempPayment.customerNumber!!, totalAmount = tempPayment.amount!!, paymentDate = txDate,
                            accountNumber = tempPayment.payerAccountNumber!!, accountName = tempPayment.payerAccountName, paymentMethod = tempPayment.paymentMode!!,
                            noticeList = noticeListDto)

                    val response  = paymentOfSelectedNoticesService.paymentOfSelectedNotice(tempPayment, paymentRequest)

                    val paidNoticeRequest = NoticeRequestDto(taxpayerNumber = tempPayment.customerNumber,
                            paymentDate = StringDateFormaterUtils.DateToString.format(LocalDate.now()))

                    checkNumberApp = LoadSettingDataToMap.settingMap.get("app.payment.notice.check.number")?.value?.toInt()?: 5
                    sleepMillis = LoadSettingDataToMap.settingMap.get("app.payment.notice.check.sleep.millis")?.value?.toLong()?: 3000

                    //We use this to reload a service if there is and error
                    var continueCheck=true
                    var noticePaidBool=false
                    var checkNumber=0

                    do {
                        //Sleep a bit before relaunch service after the fist time
                        if (checkNumber>0){
                            try {
                                Thread.sleep(sleepMillis)
                            }catch (e: Exception){
                                logger.error { e.message }
                            }
                        }

                        // Call API of paid notice to check that all notices has been effectively paid from Camcis
                        val payNoticeList = listPaidNoticeService.listPaidNotice(paidNoticeRequest, null)
                        if(payNoticeList.resultCode.equals(PäymentResultCode.S.name)){
                            val noticeNumberList = mutableSetOf<String>()
                            val paymentNumberList = mutableSetOf<String>()
                            val paymentNumberMap = hashMapOf<String, String?>()
                            payNoticeList.result().forEach { notice ->
                                if(noticeNumberToPayList.contains(notice.noticeNumber))
                                    noticeNumberList.add(notice.noticeNumber!!)
                                notice.paymentNumber?.let{
                                    paymentNumberList.add(it)
                                    paymentNumberMap.put(it, notice.camcisPaymentNumber)
                                }
                            }
                            if(noticeNumberList.containsAll(noticeNumberToPayList)){
//                                var paymentId: Long? = null
                                if (response.result!!.equals(PäymentResultCode.S.name) || paymentNumberList.contains(paymentRequest.bankPaymentNumber)) {
                                    createPaymentAndNoticeOfSuccessResponse(paymentRequest, tempPayment, selectedNoticeToPaidList)
                                    response.camcisPaymentNumber = paymentNumberMap.get(paymentRequest.bankPaymentNumber)
                                    continueCheck= false
                                    noticePaidBool=true
                                }
                            }else if(response.paymentResultCode.equals("F") || response.paymentResultCode.equals("E")) {
                                continueCheck= false
                                noticePaidBool=false
                            } else {
                                response.noticesList?.forEach {
                                    if(noticeNumberList.contains(it.noticeNumber) && !paymentNumberList.contains(paymentRequest.bankPaymentNumber)){
                                        continueCheck= false
                                        noticePaidBool=false
                                    }
                                }
                            }
                        }else if(!response.result!!.equals(PäymentResultCode.S.name) || !response.paymentResultCode!!.equals("1")){
                            continueCheck= false
                            noticePaidBool=false
                        }
                        if(response.result!!.equals(PäymentResultCode.S.name) && response.paymentResultCode!!.equals("1") && checkNumber==4){
                            val paymentId = createPaymentAndNoticeOfSuccessResponse(paymentRequest, tempPayment, selectedNoticeToPaidList)
                            continueCheck= false
                            noticePaidBool=true
                        }
                        checkNumber++
                    }while (continueCheck && checkNumber<checkNumberApp)

                    // if result is not success, Call API to cancel reservation from bank
                    if(!noticePaidBool && !(response.result!!.equals(PäymentResultCode.S.name) && response.paymentResultCode!!.equals("1"))){
                        //Call API to Cancel
                        var retryNumber=0
                        do {
                            val cancelResp = askBankCancelPaymentService.askBankCancelPayment(user, tempPayment)
                            callApiCancel= true
                            retryNumber++
                        }while (retryNumber<checkNumberApp && !cancelResp.resultCode.equals("S"))
                        throw BadRequestException(response.message)
//                        if(response.resultCode!!.equals("S"))
//                            throw BadRequestException("Error.Payment.Cancel.Sucess.", listOf(user.login!!))
                    }

                    response.paymentDate = txDate

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
        }

        throw BadRequestException("Error.User.NotFound", listOf(userLogin))
    }


//    @Transactional
//    @Synchronized
    fun createPaymentAndNoticeOfSuccessResponse(paymentRequest: PaymentProcessRequestDto, tempPayment: TempPayment,
                                                selectedNoticeList: List<SelectedNotice>): Long {

        //Create payment entity
        val payment = NoticeModelToEntity.PaymentProcessToEntity.from(paymentRequest, tempPayment)
        payment.paymentStatus = PaymentStatus.COMPLETED
        val paymentSaved = paymentRepository.save(payment)

        val noticePaidList = NoticeModelToEntity.PaymentOfNoticeModelToEntities.from(paymentRequest.noticeList)

        val noticeNumberList = mutableListOf<String>()
        noticePaidList.forEach { notice ->
            notice.payment = paymentSaved
            noticeNumberList.add(notice.noticeNumber!!)
        }

        val noticePaidListSaved = paymentOfNoticeRepository.saveAll(noticePaidList)

//        val selectedNoticeList = selectedNoticeRepository.findListNoticeNumber(noticeNumberList)

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

        val noticeMap = selectedNoticeList.map { notice -> notice.noticeNumber!! to notice }.toMap()

        noticeNumberList.clear()
        noticeSavedList.forEach { notice ->
            val listBenef = NoticeEntityToEntity.SelectedNoticeBeneficiaryEntityToEntities
                    .from(noticeMap.get(notice.noticeNumber)?.beneficiaryList ?: emptyList())
            listBenef.forEach { benef ->
                benef.notice = notice
                noticeBenefList.add(benef)
            }

            noticeNumberList.add(notice.noticeNumber?:"")
        }

//        logger.trace{"Payment finished of \n $selectedNoticeList"}

        val noticeBenefSaved = beneficiaryRepository.saveAll(noticeBenefList)

        // Delete checked customs list
//        deleteTemporaryNoticeService.deleteSelectedNoticeAfterPayment(noticeNumberList)
        deleteTemporaryNoticeService.deleteSelectedNoticeAfterPayment2(selectedNoticeList)
//        logger.trace{"Payment finished saved Benef of \n $noticeBenefSaved"}

        //Invalidate in unpaid cache
        ListUnpaidNoticeService.unpaidNoticeCache?.invalidateAll(noticeNumberList)

        return paymentSaved.id!!
    }
}