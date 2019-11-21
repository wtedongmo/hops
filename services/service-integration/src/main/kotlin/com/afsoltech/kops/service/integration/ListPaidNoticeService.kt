package com.afsoltech.kops.service.integration

import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.exception.UnauthorizedException
import com.afsoltech.core.service.utils.CheckParticipantAPIRequest
import com.afsoltech.kops.core.model.NoticeRequestDto
import com.afsoltech.kops.core.model.NoticeResponseDto
import com.afsoltech.kops.core.model.NoticeResponses
import com.afsoltech.kops.service.utils.LoadBaseDataToMap
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

    @Value("\${api.customs.epayment.listPaidNoticeUrl}")
    lateinit var listPaidNoticeURL: String

//    @Value("\${api.customs.epayment.bank.apikey}")
//    lateinit var bankApiKey: String

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
        val bankApiKey = LoadBaseDataToMap.parameterDataMap.get("api.epayment.bank.apikey") ?:
            throw UnauthorizedException("Kops.Error.Payment.Parameter.ApiKey.NotFound")
        headers.add("apikey", bankApiKey.value)

        val entity = HttpEntity(noticeRequest, headers)
        val responce = restTemplate.exchange(listPaidNoticeURL, HttpMethod.POST, entity,
                object : ParameterizedTypeReference<NoticeResponses<NoticeResponseDto>>() {})

        var responses = responce.body ?: throw BadRequestException("Kops.Error.Parameter.Value")
        logger.trace{"List of Paid Notice \n $responses"}

        return responses
    }

}