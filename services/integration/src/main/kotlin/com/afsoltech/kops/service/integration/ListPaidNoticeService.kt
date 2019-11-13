package com.nanobnk.epayment.service

import com.afsoltech.core.service.utils.CheckParticipantAPIRequest
import com.afsoltech.kops.core.model.NoticeRequestDto
import com.afsoltech.kops.core.model.NoticeResponseDto
import com.afsoltech.kops.core.model.NoticeResponses
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


@Service("list_of_paid_notice_service")
class ListPaidNoticeService(
        val restTemplate: RestTemplate, val checkParticipantAPIRequest: CheckParticipantAPIRequest
) {

    companion object : KLogging()

//    @Value("http://localhost:42601/camcis/list-paid-notice")
    @Value("\${api.customs.epayment.listPaidNoticeUrl}")
    lateinit var listPaidNoticeURL: String
//    @Value("\${app.epayment.view.other.participant.paymentnumber:false}")
//    var viewParticipantPayNumber: Boolean=false

    fun listPaidNotice(noticeRequest: NoticeRequestDto): NoticeResponses<NoticeResponseDto> {
        return listPaidNotice(noticeRequest, null)
    }


        fun listPaidNotice(noticeRequest: NoticeRequestDto, request: HttpServletRequest?): NoticeResponses<NoticeResponseDto> {

//       var paymentNoticeRequest= OutboundNoticeRequestDto(
//                noticeRequest.noticeNumber,
//               StringDateFormaterUtils.DateToString.format(noticeRequest.notificationDate),
//                noticeRequest.taxpayerNumber,
//                noticeRequest.taxpayerRepresentativeNumber,
//               StringDateFormaterUtils.DateToString.format(noticeRequest.paymentDate)
//        )

//        val inboundParticipant = checkParticipantAPIRequest.checkAPIRequest(request)


        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(noticeRequest, headers)
        val responce = restTemplate.exchange(listPaidNoticeURL, HttpMethod.POST, entity,
                object : ParameterizedTypeReference<NoticeResponses<NoticeResponseDto>>() {})

        var responses = responce.body
        logger.trace{"List of Paid Notice \n $responses"}
//        if (listPaidNotice == null || listPaidNotice.isEmpty())
//            throw BadRequestException("EPayment.Error.Parameter.Value")
//        var outboundNoticeSaveList = outboundNoticeRepository.save(NoticeModelToEntity.
//                                                        OutboundNoticeModelsToEntities.from(listPaidNotice))
//        logger.trace{"Temporary save of Paid Notice"}

//        var listModelPaidNotice = NoticeEntityToModel.NoticeModelsToEntities.from(outboundNoticeSaveList)
//        var listModelPaidNotice = NoticeModelToModel.NoticeModelsToModels.from(listPaidNotice)
//        logger.trace{"List of result Model : \n $listModelPaidNotice"}

//        if(!viewParticipantPayNumber) {
//            inboundParticipant?.let { inboundParticipant ->
//                responses.resultData?.forEach {
//                    it.paymentNumber?.let { payNumber ->
//                        if (payNumber.length > 6 && !inboundParticipant.participantCode.equals(payNumber.substring(0, 5)))
//                            it.paymentNumber = null
//                    }
//                }
//            }
//        }


        return responses
    }

}