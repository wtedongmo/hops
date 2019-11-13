package com.nanobnk.epayment.service

import com.afsoltech.kops.core.model.NoticeResponses
import com.nanobnk.epayment.entity.NoticePaymentBeneficiaryEntity
import com.nanobnk.epayment.entity.OutboundNoticePaymentBeneficiaryEntity
import com.nanobnk.epayment.entity.temporary.OutboundNoticeEntity
import com.nanobnk.epayment.model.attribute.ParticipantStatus
import com.nanobnk.epayment.model.inbound.*
import com.nanobnk.epayment.model.outbound.OutboundNoticeResponseDto
import com.nanobnk.epayment.model.outbound.OutboundUnpaidNoticeRequestDto
import com.nanobnk.epayment.repository.InboundParticipantRepository
import com.nanobnk.epayment.repository.OutboundNoticePaymentBeneficiaryRepository
import com.nanobnk.epayment.repository.OutboundNoticeRepository
import com.nanobnk.epayment.repository.temporary.CheckedNoticeRepository
import com.nanobnk.epayment.service.mapper.NoticeEntityToModel
import com.nanobnk.epayment.service.mapper.NoticeModelToEntity
import com.nanobnk.epayment.service.utils.CheckParticipantAPIRequest
import com.nanobnk.epayment.service.utils.StringDateFormaterUtils
import com.nanobnk.epayment.service.utils.TranslateUtils
import com.nanobnk.util.rest.error.BadRequestException
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest


@Service("list_of_checked_unpaid_notice_service")
class ListCheckedUnpaidNoticeService(
        val restTemplate: RestTemplate, val checkedNoticeRepository: CheckedNoticeRepository, val translateUtils: TranslateUtils,
        val listUnpaidNoticeService: ListUnpaidNoticeService, val checkParticipantAPIRequest: CheckParticipantAPIRequest
) {

    companion object : KLogging()

    //    @Value("http://localhost:42601/camcis/list-unpaid-notice")
    @Value("\${outbound.epayment.customs.listUnpaidNoticeUrl}")
    lateinit var listUnpaidNoticeURL: String

    fun listCheckedUnpaidNotice(nuiRequest: CheckedNoticeRequestDto): NoticeResponses<UnpaidNoticeResponseDto> {
        return listCheckedUnpaidNotice(nuiRequest, null)
    }


    fun listCheckedUnpaidNotice(nuiRequest: CheckedNoticeRequestDto, request: HttpServletRequest?): NoticeResponses<UnpaidNoticeResponseDto> {

        checkParticipantAPIRequest.checkAPIRequest(request)

        val checkedList = checkedNoticeRepository.findBySessionNui(nuiRequest.taxpayerNumber)

        if(checkedList.isNotEmpty()) {
            val cdaNUI = if(checkedList.first().sessionNui.equals(checkedList.first().taxPayerNumber, true))  null
                            else checkedList.first().sessionNui
            val noticeRequest = UnpaidNoticeRequestDto(noticeNumber = null, notificationDate = null,
                    taxpayerNumber = checkedList.first().taxPayerNumber, taxpayerRepresentativeNumber = cdaNUI)

//            val result = listUnpaidNoticeService.listUnpaidNotice(noticeRequest, request)

            val headers = HttpHeaders()
            headers.contentType = MediaType.APPLICATION_JSON
            val entity = HttpEntity(noticeRequest, headers)
            val responce = restTemplate.exchange(listUnpaidNoticeURL, HttpMethod.POST, entity,
                    object : ParameterizedTypeReference<NoticeResponses<UnpaidNoticeResponseDto>>() {})

            var result = responce.body
//            logger.trace { "List of Paid Notice \n $result" }
//
//
            val listUnpaidNotice = result.result()
            if (listUnpaidNotice == null || listUnpaidNotice.isEmpty())
                return result
            else if (result.resultCode!!.equals("S", true)) {
                val noticeNumberList = checkedList.map { it.noticeNumber }

                // All detail of checked unpaid notices
                val unpaidList = mutableListOf<UnpaidNoticeResponseDto>()
                listUnpaidNotice.forEach { it ->
                    if(noticeNumberList.contains(it.noticeNumber)) unpaidList.add(it)
                }
                val outboundNoticeSaveList = listUnpaidNoticeService.updateExistingNotice(unpaidList)
                result.resultData = unpaidList
                logger.trace { "Temporary save of Un Paid Notice : $unpaidList" }
            }

            return result
        }else
            return NoticeResponses(resultCode = "S", resultMsg = translateUtils.translate("checked.customs.not.found",
                    listOf(nuiRequest.taxpayerNumber)), resultData = null)

    }

}
