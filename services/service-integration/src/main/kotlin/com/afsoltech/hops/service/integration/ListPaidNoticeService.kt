package com.afsoltech.hops.service.integration

import com.afsoltech.core.entity.cap.temp.PäymentResultCode
import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.exception.UnauthorizedException
import com.afsoltech.hops.core.model.notice.NoticeRequestDto
import com.afsoltech.hops.core.model.notice.NoticeResponseDto
import com.afsoltech.hops.core.model.notice.NoticeResponses
import com.afsoltech.core.service.utils.LoadSettingDataToMap
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


@Service
class ListPaidNoticeService(
        val restTemplate: RestTemplate) { // , val checkParticipantAPIRequest: CheckParticipantAPIRequest

    companion object : KLogging()

    @Value("\${api.external.customs.epayment.listPaidNoticeUrl}")
    private lateinit var listPaidNoticeURL: String

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
//        val bankApiKey = LoadBaseDataToMap.settingMap.get("app.bank.epayment.apikey") ?:

            LoadSettingDataToMap.ePaymentApiKey?.let {
            headers.add("apikey", it)

            val entity = HttpEntity(noticeRequest, headers)
            val responce = restTemplate.exchange(listPaidNoticeURL, HttpMethod.POST, entity,
                    object : ParameterizedTypeReference<NoticeResponses<NoticeResponseDto>>() {})

            var result = responce.body ?: throw BadRequestException("Error.Parameter.Value")
            logger.trace{"List of Paid Notice \n $result"}

            if(!result.resultCode!!.equals(PäymentResultCode.S.name)){
                throw BadRequestException(result.resultMsg)
            }
            return result
        }
         throw UnauthorizedException("Error.Payment.Parameter.ApiKey.NotFound")
    }

}