package com.nanobnk.epayment.portal.service

import com.nanobnk.epayment.entity.DeclarationTypeEntity
import com.nanobnk.epayment.entity.IssuerOfficeEntity
import com.nanobnk.epayment.entity.NoticeTypeEntity
import com.nanobnk.epayment.model.inbound.NoticePortalResponseDto
import com.nanobnk.epayment.model.inbound.NoticeRequestDto
import com.nanobnk.epayment.model.inbound.NoticeResponseDto
import com.nanobnk.epayment.model.inbound.NoticeResponses
import com.nanobnk.epayment.portal.service.mapper.PortalNoticeModelToModel
import com.nanobnk.epayment.portal.utils.LoadBaseDataFromDB
import com.nanobnk.epayment.repository.DeclarationTypeRepository
import com.nanobnk.epayment.repository.IssuerOfficeRepository
import com.nanobnk.epayment.repository.NoticeTypeRepository
import com.nanobnk.epayment.repository.PaymentCategoryRepository
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.text.DecimalFormat


@Service("list_of_paid_notice_service_core_portal")
class PortalListPaidNoticeService(
        val restTemplate: RestTemplate
) {

    companion object : KLogging()

    //    @Value("http://localhost:42601/camcis/list-paid-notice")
    @Value("\${outbound.epayment.customs.listPaidNoticeUrl}")
    lateinit var listPaidNoticeURL: String

    @Autowired
    lateinit var loadBaseDataFromDB: LoadBaseDataFromDB


    fun listPaidNotice(noticeRequest: NoticeRequestDto): List<NoticePortalResponseDto>? {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(noticeRequest, headers)
        val responce = restTemplate.exchange(listPaidNoticeURL, HttpMethod.POST, entity,
                object : ParameterizedTypeReference<NoticeResponses<NoticeResponseDto>>() {})

        val result = responce.body
        var listPaidNotice = result.result()
        logger.trace{"List of Paid Notice \n $listPaidNotice"}
        if (listPaidNotice == null || listPaidNotice.isEmpty())
            return null
//            //throw BadRequestException("EPayment.Error.Parameter.Value")

        var listModelPaidNotice = PortalNoticeModelToModel.NoticeModelsToModels.from(listPaidNotice)

        val decf = DecimalFormat("#,##0")
        loadBaseDataFromDB.loadBaseData()
        listModelPaidNotice.forEach { notice ->
            notice.noticeType = loadBaseDataFromDB.noticeTypeMap.get(notice.noticeType)?:notice.noticeType
            notice.declarationType = loadBaseDataFromDB.declarationTypeMap.get(notice.declarationType)?:notice.declarationType
            notice.issuerOffice = loadBaseDataFromDB.issuerOfficeMap.get(notice.issuerOffice)?:notice.issuerOffice
            notice.paymentCategory = loadBaseDataFromDB.paymentCategoryMap.get(notice.paymentCategory)?:notice.paymentCategory


        }
        logger.trace{"List of result Model : \n $listModelPaidNotice"}

        return listModelPaidNotice
//        return NoticeResponses(
//                result.resultCode,
//                result.resultMsg,
//                listModelPaidNotice
//        )
    }

}