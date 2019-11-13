package com.nanobnk.epayment.service

import com.nanobnk.epayment.entity.InboundParticipantEntity
import com.nanobnk.epayment.entity.NoticePaymentBeneficiaryEntity
import com.nanobnk.epayment.entity.OutboundNoticePaymentBeneficiaryEntity
import com.nanobnk.epayment.entity.temporary.InboundPaymentEntity
import com.nanobnk.epayment.entity.temporary.OutboundNoticeEntity
import com.nanobnk.epayment.entity.temporary.PäymentResultCode
import com.nanobnk.epayment.model.attribute.NoticeSource
import com.nanobnk.epayment.model.attribute.ParticipantStatus
import com.nanobnk.epayment.model.attribute.PaymentStatus
import com.nanobnk.epayment.model.inbound.NoticePaymentSummaryDto
import com.nanobnk.epayment.model.inbound.PaymentProcessRequestDto
import com.nanobnk.epayment.model.inbound.PaymentProcessResponseDto
import com.nanobnk.epayment.model.outbound.OutboundPaymentOfNoticeResponsesDto
import com.nanobnk.epayment.repository.*
import com.nanobnk.epayment.service.mapper.NoticeEntityToEntity
import com.nanobnk.epayment.service.mapper.NoticeModelToEntity
import com.nanobnk.epayment.service.mapper.PaymentProcessMapper
import com.nanobnk.epayment.service.utils.StringDateFormaterUtils
import com.nanobnk.util.rest.error.BadRequestException
import com.nanobnk.util.rest.error.ConflictException
import com.nanobnk.util.rest.error.ForbiddenException
import com.nanobnk.util.rest.error.RestException
import com.nanobnk.util.rest.util.ensureNotNull
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import java.sql.Timestamp
import java.time.LocalDateTime


@Service("payment_of_notice_service")
class PaymentProcessOfNoticesService(
        val restTemplate: RestTemplate, val noticeRepository: NoticeRepository, val beneficiaryRepository: NoticePaymentBeneficiaryRepository,
        val outboundNoticeRepository: OutboundNoticeRepository, val paymentOfNoticeRepository: PaymentOfNoticeRepository,
        val inboundPaymentRepository: InboundPaymentRepository, val paymentRepository: PaymentRepository, val paymentMethodRepository: PaymentMethodRepository,
        val inboundParticipantRepository: InboundParticipantRepository, val outbNoticeBenefRepository: OutboundNoticePaymentBeneficiaryRepository,
        val deleteTemporaryNoticeService: DeleteTemporaryNoticeService
) {

    companion object : KLogging()

    //    @Value("http://localhost:42601/camcis/payment-of-notice")
    @Value("\${outbound.epayment.customs.paymentProcessingURL}")
    lateinit var paymentOfNoticeURL: String

    @Value("\${api.epayment.min.difference.amount}")
    lateinit var minDiffAmount: String

    @Value("\${api.epayment.check.bank.payment.number.format}")
    var checkBankPaymentNumberFormat: Boolean=false

    @Value("\${api.epayment.payment.date.diff.time.abs}")
    var maxTimeDiffAbsPaymentDate: Long=14400000

    var paymentMethodCode: String? =null
    var participant: InboundParticipantEntity?=null

    fun paymentOfNoticeProcess(paymentRequest: PaymentProcessRequestDto): PaymentProcessResponseDto? {

        /*Check Participant Exist*/
        participant = inboundParticipantRepository.findByParticipantCode(paymentRequest.bankCode)
        if (participant == null)
            throw BadRequestException("EPayment.Error.Payment.BankCode")
        else if(!participant!!.participantStatus.equals(ParticipantStatus.ACTIVE)){
            throw ForbiddenException("EPayment.Error.Participant.Forbit")
        }

        if(checkBankPaymentNumberFormat){
            checkBankPaymentNumber(paymentRequest)
        }

        // check Payment Method
        var paymentMethod = paymentMethodRepository.findByCode(paymentRequest.paymentMethod)
        paymentMethodCode = if (paymentMethod.isNullOrEmpty()) {
                paymentMethod = paymentMethodRepository.findByName(paymentRequest.paymentMethod)
                if(paymentMethod.isNullOrEmpty())
                    throw BadRequestException("EPayment.Error.Payment.Method")
                else {
                    if(paymentRequest.paymentMethod.equals("CAMCIS", true))
                        throw BadRequestException("EPayment.Error.Payment.Method.Not_allowed")
                    paymentMethod.first().code ?: ""
                }
            } else {
                if(paymentMethod.first().name.equals("CAMCIS", true))
                    throw BadRequestException("EPayment.Error.Payment.Method.Not_allowed")
                paymentRequest.paymentMethod
            }

        /*Check amount of Payment and detail*/
        verifyTotalAmountAndDetail(paymentRequest)

        val outboundNoticeEntityList = getListNoticeCheckExistAndAmount(paymentRequest.noticeList)
        /*Check if list is not null and create if customs not already exists*/

//        createNoticeAndBeneficiary(noticeEntityList)

        /*check if payment not already exists, verify if it is the same bank, if yes update,
                   if no update existing to cancel status and create new*/
        var inboundPaymentEntity = checkInboundPaymentExists(paymentRequest)

        if (inboundPaymentEntity == null)
            inboundPaymentEntity = createInboundPayment(paymentRequest)

        /* Send Payment To CAMCIS */
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        headers.add("apikey", participant!!.inboundParticipantApiKeyValue)
        //val paymentRequestToSend = PaymentProcessMapper.ModelMapPaymentProcess.map(paymentRequest)
        val httpEntity = HttpEntity(paymentRequest, headers)
        var response : ResponseEntity<OutboundPaymentOfNoticeResponsesDto>? = null
        try {
            response = restTemplate.exchange(paymentOfNoticeURL, HttpMethod.POST, httpEntity,
                    object : ParameterizedTypeReference<OutboundPaymentOfNoticeResponsesDto>() {})
        }catch (ex: Exception){
            inboundPaymentEntity.paymentStatus = PaymentStatus.FAILED
            inboundPaymentRepository.save(inboundPaymentEntity)
            logger.error { "Exception:"+ex.message+ "\n"+ ex.printStackTrace()}
            throw ex
        }

        val httpStatus = response!!.statusCode
        inboundPaymentEntity.outboundResponseStatus = httpStatus?.value()

        val responsePayment = response.body
        logger.trace { "\tResponses Payment of Camcis \n\t $responsePayment" }

        if (!httpStatus.toString().startsWith("2") || responsePayment == null) {
            inboundPaymentEntity.paymentStatus = PaymentStatus.FAILED
            inboundPaymentRepository.save(inboundPaymentEntity)
            if (!httpStatus.toString().startsWith("2"))
                throw RestException(httpStatus, httpStatus.reasonPhrase)
            if (responsePayment == null)
                throw BadRequestException("EPayment.Error.Parameter.Value")
        }

        /*Update epayment database payment and customs checking response  */
        val inbPaymentSaved = updateInboundPaymentFromResponse(responsePayment, inboundPaymentEntity)
        //updateNoticeListFromResponse(responsePayment, noticeEntityList)

        //If payment success, create payment and notices
        var paymentId: Long? = null
        if (inbPaymentSaved.resultCode!!.equals(PäymentResultCode.S.name)) {
            paymentId = createPaymentAndNoticeOfSuccessResponse(paymentRequest, outboundNoticeEntityList)
        }
        responsePayment.epaymentId = paymentId
        /* Send back responses, transform to model*/

        val responseService = PaymentProcessMapper.PaymentResponseModelToModel.from(responsePayment)
        return responseService
    }

//    @Transactional
//    fun doOutBoundPayment(paymentRequest: PaymentProcessRequestDto) : InboundPaymentEntity?{
//
//        return null
//    }

    private fun checkBankPaymentNumber(paymentRequest: PaymentProcessRequestDto) {
        val bankPaymentNumber = paymentRequest.bankPaymentNumber
        val bankCode = paymentRequest.bankCode
        var payDateTime: LocalDateTime = StringDateFormaterUtils.ParsePaymentDate.parse(paymentRequest.paymentDate)

//        val paymentDate = StringDateFormaterUtils.DateToString.format(paymentRequest.paymentDate.toLocalDate())
        val paymentDate = StringDateFormaterUtils.DateToString.format(payDateTime.toLocalDate())

        if(bankPaymentNumber.length<15 || !bankPaymentNumber.substring(0,5).equals(bankCode, true) ||
                !bankPaymentNumber.substring(5,13).equals(paymentDate, true)){
            throw BadRequestException("EPayment.Error.Payment.BankPaymentNumber.Format", listOf(bankPaymentNumber))
        }
    }

    private fun checkInboundPaymentExists(paymentRequest: PaymentProcessRequestDto): InboundPaymentEntity? {

        val bankPaymentNumber = paymentRequest.bankPaymentNumber
        ensureNotNull(bankPaymentNumber)
        if(bankPaymentNumber.length<10){
            throw BadRequestException("EPayment.Error.Payment.BankPaymentNumber", listOf(bankPaymentNumber))
        }
        val inbPaymentEntityList = inboundPaymentRepository.findByInboundPaymentNumber(bankPaymentNumber)
        inbPaymentEntityList?.let {
            inbPaymentEntityList.forEach { payment ->
                when (payment.paymentStatus) {
                    PaymentStatus.COMPLETED -> throw ConflictException("EPayment.Error.Payment.Exists.Number")
                    PaymentStatus.ALREADY_PAID -> throw ConflictException("EPayment.Error.Payment.Exists.Number")
                    PaymentStatus.IN_PROGRESS -> throw ConflictException("EPayment.Error.Payment.In_Progress")
                    PaymentStatus.NUMBER_EXISTS -> throw ConflictException("EPayment.Error.Payment.Exists.Status", listOf(payment.paymentStatus
                            ?: ""))
                    PaymentStatus.CANCELED_NOTICE -> throw BadRequestException("EPayment.Error.Payment.Exists.Status", listOf(payment.paymentStatus
                            ?: ""))
                    PaymentStatus.NOTICE_NUMBER_ERROR -> throw BadRequestException("EPayment.Error.Payment.Exists.Status", listOf(payment.paymentStatus
                            ?: ""))
                    else -> {
                        payment.paymentResultCode?.let {
                            throw BadRequestException("EPayment.Error.Payment.Exists.Status.ResultCode", listOf(payment.paymentStatus
                                    ?: "", payment.paymentResultCode ?: ""))
                        }
                        if (payment.outboundCustomerCode != paymentRequest.taxpayerNumber)
                            throw ConflictException("EPayment.Error.Payment.Exists.TaxPayer")
                        if (payment.inboundParticipantCode != paymentRequest.bankCode)
                            throw ConflictException("EPayment.Error.Payment.Exists.Bank_Code")

/*Update payment entity infos with the new request*/

                        if (payment.totalAmount != paymentRequest.totalAmount) {
//                            val mapDetail = hashMapOf<String?, Long?>()
                            val paymentEntityUpdate = requestToInboundEntity(paymentRequest)
                            paymentEntityUpdate.paymentId = payment.paymentId

                            return inboundPaymentRepository.save(paymentEntityUpdate)

                        } else if (payment.payorAccountNumber != paymentRequest.accountNumber) {
                            payment.payorAccountName = paymentRequest.accountName
                            payment.payorAccountNumber = paymentRequest.accountNumber
                            return inboundPaymentRepository.save(payment)
                        }

                        return payment
                    }
                }
            }
        }
        return null
    }

    private fun verifyTotalAmountAndDetail(paymentRequest: PaymentProcessRequestDto) {

//        val totalAmount = paymentRequest.noticeList.map { it.noticeAmount }.reduce { acc, item -> acc + item }
        val nowTime = LocalDateTime.now()
        var payDateTime= StringDateFormaterUtils.ParsePaymentDate.parse(paymentRequest.paymentDate)

        val paymentTime = Timestamp.valueOf(payDateTime).getTime()
        val diffTime =  Math.abs(System.currentTimeMillis() - paymentTime)
        if(maxTimeDiffAbsPaymentDate<diffTime) {
            throw BadRequestException("EPayment.Error.Payment.PaymentDate.Bad")
        }
//        if (nowTime.isBefore(paymentRequest.paymentDate)) {
//            throw BadRequestException("EPayment.Error.Payment.PaymentDate")
//        }

//        if (nowTime.dayOfYear!=paymentRequest.paymentDate.dayOfYear && nowTime.hour>8) {
//            throw BadRequestException("EPayment.Error.Payment.PaymentDate.Old")
//        }

        if(paymentRequest.noticeList.isEmpty())
            throw BadRequestException("EPayment.Error.Payment.NoticeList.Empty")

        val totalAmount = paymentRequest.noticeList.sumByDouble { it -> it.noticeAmount.toDouble() }
        if (Math.abs(paymentRequest.totalAmount.toDouble().minus(totalAmount)) > Integer.parseInt(minDiffAmount)) {
            throw BadRequestException("EPayment.Error.Payment.Amount")
        }
    }


/*private fun checkDataRequest(paymentRequest: PaymentProcessRequestDto) {
        "".ifNotBlank {}
        val nullable: String? = ""
        val checked = ensureNotNull(nullable) { "" }
    }*/


    private fun getListNoticeCheckExistAndAmount(noticeSumList: List<NoticePaymentSummaryDto>): List<OutboundNoticeEntity> {

        //val tempNoticeEntityList = mutableListOf<OutboundNoticeEntity>()
        //val noticeEntityList = mutableListOf<NoticeEntity>()
        val toPayNoticeIdList = mutableListOf<Long>()
        val noticeNumberList = mutableListOf<String>()
        val mapNotice = hashMapOf<String, NoticePaymentSummaryDto>()
        val mapNoticeIdNumber = hashMapOf<Long, String>()
        noticeSumList.forEach { item ->
            toPayNoticeIdList.add(item.noticeId)
            noticeNumberList.add(item.noticeNumber)
            mapNotice.put(item.noticeNumber, item)
            mapNoticeIdNumber.put(item.noticeId, item.noticeNumber)
        }
        val tempNoticeEntityList = outboundNoticeRepository.findAll(toPayNoticeIdList)
        ensureNotNull(tempNoticeEntityList) { "EPayment.Error.Payment.NotExists.Notice" }

        if (tempNoticeEntityList.size != toPayNoticeIdList.size)
            throw BadRequestException("EPayment.Error.Payment.NotExists.Notices")

        /*Check Notice Number and Amount*/
        tempNoticeEntityList.forEach { notice ->
            val noticeRequest = mapNotice.get(notice.noticeNumber)
            if (noticeRequest== null || !noticeRequest.noticeNumber.equals(notice.noticeNumber)) {
                val noticeNumber = mapNoticeIdNumber.get(notice.noticeId)
                throw BadRequestException("EPayment.Error.Payment.Notice.NotFound", listOf(noticeNumber ?: ""))
            }

            if(!noticeRequest.noticeId.equals(notice.noticeId)){
                throw BadRequestException("EPayment.Error.Payment.Notice.NotMatch", listOf(noticeRequest.noticeId, noticeRequest.noticeNumber))
            }

            if (Math.abs(noticeRequest?.noticeAmount?.toDouble()!!.minus(notice.amount?.toDouble()!!)) > Integer.parseInt(minDiffAmount)) {
                val amount = noticeRequest?.noticeAmount
                logger.trace { "\tNotice Amount ${notice.amount}, User Amount $amount" }
                throw BadRequestException("EPayment.Error.Payment.Notice.Amount", listOf(notice.noticeNumber ?: ""))
            }
        }

        /*Check if the notices exist */

        val noticeListSearch = noticeRepository.findByListNoticeNumber(noticeNumberList)
        noticeListSearch?.let {
            var noticeSt = StringBuilder()
            noticeListSearch.forEach { item ->
                noticeSt.append(item.noticeNumber).append(", ")
            }
            if (noticeSt.toString().length > 0)
                throw ConflictException("EPayment.Error.Payment.Notice.Paid", listOf(noticeSt))
//            noticeListSerach.forEach { item ->
//                when (item.noticeStatus) {
//                    PaymentStatus.COMPLETED -> throw ConflictException("EPayment.Error.Payment.Notice.Paid", listOf(item.noticeNumber?:""))
//                    PaymentStatus.ALREADY_PAID -> throw ConflictException("EPayment.Error.Payment.Notice.Paid", listOf(item.noticeNumber?:""))
//                    PaymentStatus.CANCELED_NOTICE -> throw BadRequestException("EPayment.Error.Payment.Notice.Canceled", listOf(item.noticeNumber?:""))
//                    PaymentStatus.IN_PROGRESS -> {
// /*Verify if the payment is in progress or it was because ???*/
//
//                        val payOfNoticeEntity = paymentOfNoticeRepository.findByOutboundNoticeNumber(item.noticeNumber!!)
//                        payOfNoticeEntity?.let {
//                            val payList = payOfNoticeEntity.map { item2 -> item2.payment?.paymentId }
//                            val payListEntity = paymentRepository.findAll(payList)
//                            payListEntity?.forEach { it ->
//                                if(it?.paymentStatus==PaymentStatus.IN_PROGRESS)
//                                    throw ConflictException("EPayment.Error.Payment.Notice.In_progess", listOf(item.noticeNumber?:""))
//                            }
//                        }
//
//                    }
//                    else -> print("")
//                }
//            }

            return tempNoticeEntityList
        }

/*Copy of temp customs entity to customs entity to create customs */

//        var noticeList = mutableListOf<NoticeEntity>()
//        if (noticeListSerach != null) {
//            noticeList = noticeListSerach.toMutableList()
//            noticeList.forEach { item ->
//                if (noticeNumberList.contains(item.noticeNumber))
//                    noticeNumberList.remove(item.noticeNumber)
//            }
//            var noticeToAdd = mutableListOf<OutboundNoticeEntity>()
//            noticeNumberList?.let {
//                tempNoticeEntityList.forEach { item ->
//                    if (noticeNumberList.contains(item.noticeNumber))
//                        noticeToAdd.add(item)
//                }
//                if (noticeToAdd.size > 0)
//                    noticeList.addAll(NoticeEntityToEntity.OutboundNoticeEntityToNoticeEntities.from(noticeToAdd))
//            }
//        } else
//            noticeList = NoticeEntityToEntity.OutboundNoticeEntityToNoticeEntities.from(tempNoticeEntityList)
//
//        noticeList.forEach { customs ->
//            customs.listNoticeBeneficiary.forEach { benef ->
//                benef.customs = customs
//            }
//        }
//        return noticeRepository.save(noticeList)

    }


    private fun createInboundPayment(paymentRequest: PaymentProcessRequestDto): InboundPaymentEntity {
        val inbPaymentEntity = requestToInboundEntity(paymentRequest)

        return inboundPaymentRepository.save(inbPaymentEntity)

    }

    private fun requestToInboundEntity(paymentRequest: PaymentProcessRequestDto): InboundPaymentEntity {
        var payDateTime = StringDateFormaterUtils.ParsePaymentDate.parse(paymentRequest.paymentDate)

        val entity = InboundPaymentEntity(
                inboundPaymentNumber = paymentRequest.bankPaymentNumber,
                inboundParticipantCode = paymentRequest.bankCode,
                inboundParticipantName = paymentRequest.bankName,
                outboundCustomerCode = paymentRequest.taxpayerNumber,
                payorAccountNumber = paymentRequest.accountNumber,
                payorAccountName = paymentRequest.accountName,
                paymentMethod = paymentMethodCode,
                totalAmount = paymentRequest.totalAmount,
                paymentDate = payDateTime,
                noticesList = paymentRequest.noticeList.toString()
        )
        return entity
    }

    private fun updateInboundPaymentFromResponse(responsePayment: OutboundPaymentOfNoticeResponsesDto?, inbPaymentEntity: InboundPaymentEntity):
            InboundPaymentEntity {

        inbPaymentEntity.resultCode = responsePayment?.resultCode
        inbPaymentEntity.resultMessage = responsePayment?.resultData?.resultMsg

        inbPaymentEntity.paymentResultCode = responsePayment?.resultCode
        inbPaymentEntity.paymentResultMessage = if (responsePayment?.resultData != null && responsePayment?.resultData?.resultMsg != null) {
            responsePayment?.resultData?.resultMsg
        } else null

        var status = PaymentStatus.IN_PROGRESS
        if (responsePayment?.resultCode.equals("F", true)) {
            responsePayment?.resultData?.resultCode?.let {
                status = when (responsePayment?.resultData?.resultCode) {
                    "02" -> PaymentStatus.NUMBER_EXISTS
                    "03" -> PaymentStatus.NO_LIST
                    "04" -> PaymentStatus.AMOUNT_ERROR
                    else -> PaymentStatus.FAILED
                }
            }

        } else if (responsePayment?.resultCode.equals("E", true))
            status = PaymentStatus.SYSTEM_ERROR
        else if (responsePayment?.resultCode.equals("S", true))
            status = PaymentStatus.COMPLETED
        else
            status = PaymentStatus.UNKNOWN_ERROR

        inbPaymentEntity.paymentStatus = status
        inbPaymentEntity.paymentResultData = responsePayment?.resultData.toString()
        participant?.let {
            inbPaymentEntity.inboundParticipantId=it.inboundParticipantId
        }

        val inbPaymentSaved = inboundPaymentRepository.save(inbPaymentEntity)

        return inbPaymentSaved
    }


    @Transactional
    @Synchronized
    fun createPaymentAndNoticeOfSuccessResponse(paymentRequest: PaymentProcessRequestDto, outboundNoticeList: List<OutboundNoticeEntity>): Long {

        //Create payment entity
        val paymentEntity = NoticeModelToEntity.PaymentProcessModelToEntity.from(paymentRequest)
        paymentEntity.paymentStatus = PaymentStatus.COMPLETED
        paymentEntity.paymentMethod = paymentMethodCode
        participant?.let {
            paymentEntity.inboundParticipantId=it.inboundParticipantId
        }
        val paymentSaved = paymentRepository.save(paymentEntity)

        val noticePaidList = NoticeModelToEntity.NoticeOfPaymentModelToEntities.from(paymentRequest.noticeList)
        noticePaidList.forEach { notice ->
            notice.payment = paymentSaved
        }

        val noticePaidListSaved = paymentOfNoticeRepository.save(noticePaidList)


        val noticesList = NoticeEntityToEntity.OutboundNoticeEntityToNoticeEntities.from(outboundNoticeList)
        noticesList.forEach { notice ->
            notice.noticeStatus = PaymentStatus.COMPLETED
            notice.noticeSource = NoticeSource.E_PAYMENT
            notice.paymentId = paymentSaved.paymentId
            notice.paymentDate = paymentSaved.paymentDate
            notice.paymentMethod = paymentSaved.paymentMethod
            notice.paymentCategory = "004"
            notice.inboundParticipantId = paymentSaved.inboundParticipantId
            notice.paymentAmount = notice.amount // noticePaidListSaved.find{ it -> it.outboundNoticeNumber.equals(customs.noticeNumber) }?.amount
            notice.paymentNumber = paymentSaved.inboundPaymentNumber
        }
        val noticeSavedList = noticeRepository.save(noticesList)

        val noticeBenefList = mutableListOf<NoticePaymentBeneficiaryEntity>()

        val outbNoticeMap = outboundNoticeList.map { notice -> notice.noticeNumber!! to notice }.toMap()

//        val noticeMap = noticeSavedList.map { customs -> customs.noticeNumber!! to customs }.toMap()

        val noticeNumberList = mutableListOf<String>()
        noticeSavedList.forEach { notice ->
            val listBenef = NoticeEntityToEntity.OutboundNoticeBeneficiaryEntityToEntities
                    .from(outbNoticeMap.get(notice.noticeNumber)?.listNoticeBeneficiary ?: emptyList())
            listBenef.forEach { benef ->
                benef.notice = notice
                noticeBenefList.add(benef)
            }

            noticeNumberList.add(notice.noticeNumber?:"")
        }

        val noticeBenefSaved = beneficiaryRepository.save(noticeBenefList)

        //Delete OutboundNotice and beneficiary
//        val outbBenefIdList = mutableListOf<OutboundNoticePaymentBeneficiaryEntity>()
//        outboundNoticeList.forEach { it ->
//            outbBenefIdList.addAll(it.listNoticeBeneficiary) //.map { b -> b.beneficiaryId!! }
//        }
//
//        outbNoticeBenefRepository.delete(outbBenefIdList)
//        outboundNoticeRepository.delete(outboundNoticeList)

        // Delete checked customs list
        deleteTemporaryNoticeService.deleteCheckedNoticeAfterPayment(noticeNumberList)

        return paymentSaved.paymentId!!
    }


}

