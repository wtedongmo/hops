package com.afsoltech.kops.service.integration

import com.afsoltech.core.exception.UnauthorizedException
import com.afsoltech.core.service.utils.CheckParticipantAPIRequest
import com.afsoltech.core.service.utils.TranslateUtils
import com.afsoltech.kops.core.entity.customs.temp.SelectedNoticeBeneficiary
import com.afsoltech.kops.core.model.NoticeResponses
import com.afsoltech.kops.core.model.integration.OutSelectedNoticeRequestDto
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.core.repository.temp.SelectedNoticeBeneficiaryRepository
import com.afsoltech.kops.core.repository.temp.SelectedNoticeRepository
import com.afsoltech.kops.service.mapper.NoticeModelToEntity
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
import javax.transaction.Transactional


@Service("list_of_selected_unpaid_notice_service")
class RetrieveSelectedUnpaidNoticeService(
        val restTemplate: RestTemplate, val selectedNoticeRepository: SelectedNoticeRepository,
        val selectedNoticeBeneficiaryRepository: SelectedNoticeBeneficiaryRepository, val checkParticipantAPIRequest: CheckParticipantAPIRequest
) {

    companion object : KLogging()

    @Value("\${api.customs.epayment.selectedUnpaidNoticeUrl}")
    lateinit var selectedUnpaidNoticeUrl: String
    

    fun listSelectedUnpaidNotice(selectedRequest: OutSelectedNoticeRequestDto, userLogin: String, request: HttpServletRequest?): NoticeResponses<UnpaidNoticeResponseDto>? {

        checkParticipantAPIRequest.checkAPIRequest(request)
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val bankApiKey = LoadBaseDataToMap.parameterDataMap.get("api.epayment.bank.apikey") ?:
            throw UnauthorizedException("Kops.Error.Payment.Parameter.ApiKey.NotFound")
        headers.add("apikey", bankApiKey.value)

        val entity = HttpEntity(selectedRequest, headers)
        val responce = restTemplate.exchange(selectedUnpaidNoticeUrl, HttpMethod.POST, entity,
                object : ParameterizedTypeReference<NoticeResponses<UnpaidNoticeResponseDto>>() {})

        var result = responce.body
        logger.trace { "List of selected unPaid Notice \n $result" }
//
//
        val listUnpaidNotice = result?.result()
        if (listUnpaidNotice == null || listUnpaidNotice.isEmpty())
            return result
        else if (result?.resultCode!!.equals("S", true)) {
            updateExistingNotice(listUnpaidNotice, userLogin)
            logger.trace { "Temporary save of Un Paid Notice : $listUnpaidNotice" }
        }

        return result
    }

    @Transactional
    @Synchronized
    fun updateExistingNotice(listUnpaidNotice: List<UnpaidNoticeResponseDto>?, userLogin: String) { //: MutableList<OutboundNoticeEntity>

        val listNoticeNumber = listUnpaidNotice?.map { it -> it.noticeNumber!! } ?: emptyList()
        val listExistingNotice = selectedNoticeRepository.findListNoticeNumber(listNoticeNumber)
        val listEntity = NoticeModelToEntity.SelectedNoticeModelsToEntities.from(listUnpaidNotice ?: emptyList())
        val noticeIdBenefMap = hashMapOf<String?, Long?>()

        if(listExistingNotice.isNotEmpty()) {
            val noticeIdMap = hashMapOf<String?, Long?>()
            listExistingNotice.forEach { it ->
                noticeIdMap.put(it.noticeNumber, it.id)
                it.beneficiaryList.forEach { b ->
                    noticeIdBenefMap.put(it.noticeNumber+"#"+b.beneficiaryCode, b.id)
                }
            }
            listEntity.forEach { it ->
                if (noticeIdMap.containsKey(it.noticeNumber)) {
                    it.id = noticeIdMap.get(it.noticeNumber)
                }
            }
        }

        listEntity.forEach { it ->
            it.userLogin = userLogin
        }
        val selectedNoticeList = selectedNoticeRepository.saveAll(listEntity)
        logger.trace { "\n\nResult Unpaid Notice without beneficiary :\n$selectedNoticeList" }

        val noticeBenefMap = listUnpaidNotice?.map { notice -> notice.noticeNumber!! to notice.beneficiaryList!!
        }!!.toMap()

        val noticeBenefList = mutableListOf<SelectedNoticeBeneficiary>()

        selectedNoticeList.forEach { notice ->
            val listBenef = NoticeModelToEntity.OutboundNoticeBeneficiaryModelToEntities
                    .from(noticeBenefMap.get(notice.noticeNumber)?: emptyList())
            listBenef.forEach { benef ->
                if(noticeIdBenefMap.contains(notice.noticeNumber+"#"+benef.beneficiaryCode)){
                    benef.id=noticeIdBenefMap.get(notice.noticeNumber+"#"+benef.beneficiaryCode)
                }
                benef.selectedNotice = notice
                noticeBenefList.add(benef)
            }
        }

        val noticeBenefSaved = selectedNoticeBeneficiaryRepository.saveAll(noticeBenefList)
        val noticeMap = selectedNoticeList.map { notice ->
            notice.noticeNumber!! to notice.id!!
        }.toMap()

        listUnpaidNotice.forEach { notice ->
            notice.noticeId = noticeMap.get(notice.noticeNumber)
        }
        logger.trace { "\n\nResult Unpaid Beneficiary :\n$listUnpaidNotice" }

    }

}
