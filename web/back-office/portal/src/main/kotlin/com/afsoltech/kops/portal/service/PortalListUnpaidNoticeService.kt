package com.nanobnk.epayment.portal.service

import com.nanobnk.epayment.entity.temporary.CheckedNoticeEntity
import com.nanobnk.epayment.model.inbound.NoticeResponses
import com.nanobnk.epayment.model.inbound.UnpaidNoticePortalResponseDto
import com.nanobnk.epayment.model.inbound.UnpaidNoticeRequestDto
import com.nanobnk.epayment.model.inbound.UnpaidNoticeResponseDto
import com.nanobnk.epayment.portal.service.mapper.PortalNoticeModelToModel
import com.nanobnk.epayment.portal.utils.LoadBaseDataFromDB
import com.nanobnk.epayment.repository.temporary.CheckedNoticeRepository
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate


@Service("list_of_unpaid_notice_service_portal")
class PortalListUnpaidNoticeService(val restTemplate: RestTemplate, val checkedNoticeRepository: CheckedNoticeRepository) {

    companion object : KLogging()

    @Value("\${outbound.epayment.customs.listUnpaidNoticeUrl}")
    lateinit var listUnpaidNoticeURL: String

    @Autowired
    lateinit var loadBaseDataFromDB: LoadBaseDataFromDB

    fun listUnpaidNotice(noticeRequest: UnpaidNoticeRequestDto): List<UnpaidNoticePortalResponseDto>? {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(noticeRequest, headers)
        val responce = restTemplate.exchange(listUnpaidNoticeURL, HttpMethod.POST, entity,
                object : ParameterizedTypeReference<NoticeResponses<UnpaidNoticeResponseDto>>() {})

        val result = responce.body
        var listUnpaidNotice = responce.body.result()
        logger.trace { "List of Paid Notice \n $listUnpaidNotice" }

        if (listUnpaidNotice == null || listUnpaidNotice.isEmpty())
            return null
            //throw BadRequestException("EPayment.Error.Parameter.Value")

        var listModelUnpaidNotice = PortalNoticeModelToModel.UnpaidNoticeModelsToModels.from(listUnpaidNotice)

        loadBaseDataFromDB.loadBaseData()
        listModelUnpaidNotice.forEach { notice ->
            notice.noticeType = loadBaseDataFromDB.noticeTypeMap.get(notice.noticeType)?:notice.noticeType
            notice.declarationType = loadBaseDataFromDB.declarationTypeMap.get(notice.declarationType)?:notice.declarationType
            notice.issuerOffice = loadBaseDataFromDB.issuerOfficeMap.get(notice.issuerOffice)?:notice.issuerOffice
        }

        logger.trace { "List of result Model : \n $listModelUnpaidNotice" }

        return listModelUnpaidNotice
//        return NoticeResponses(
//                result.resultCode,
//                result.resultMsg,
//                listModelUnpaidNotice
//        )
    }


    @Transactional
    @Synchronized
    fun saveCheckedNotices(sessionNUI: String, checkedNotices: List<String>) { //:Boolean

        val existingList = checkedNoticeRepository.findBySessionNui(sessionNUI)
        if(existingList.isNotEmpty()){
            checkedNoticeRepository.delete(existingList)
        }
        val listToSave= mutableListOf<CheckedNoticeEntity>()
        checkedNotices.forEach { it ->
            val tabs = it.split("#")
            val checkedNoticeEntity = CheckedNoticeEntity(sessionNui = sessionNUI, noticeNumber = tabs.first(), taxPayerNumber = tabs.get(1))
            listToSave.add(checkedNoticeEntity)
        }
        if (listToSave.isNotEmpty()){
            val savedList = checkedNoticeRepository.save(listToSave)
        }
    }

}
