package com.afsoltech.kops.service.ws

import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.core.repository.temp.SelectedNoticeBeneficiaryRepository
import com.afsoltech.kops.core.repository.temp.SelectedNoticeRepository
import com.afsoltech.kops.service.mapper.NoticeModelToEntity
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SaveSelectedNoticeService(val selectedNoticeRepository: SelectedNoticeRepository,
                                val selectedNoticeBeneficiaryRepository: SelectedNoticeBeneficiaryRepository) {

    companion object : KLogging()

    @Transactional
    @Synchronized
    fun saveSelectedNotices(userName: String, selectedNoticeList: List<String>) { //:Boolean

        val selectedUnpaidNoticeList = mutableListOf<UnpaidNoticeResponseDto>()
        selectedNoticeList.forEach {
            selectedUnpaidNoticeList.add(ListUnpaidNoticeService.unpaidNoticeCache!!.get(it))
        }
        val existingList = selectedNoticeRepository.findByUserLogin(userName)
        if(existingList.isNotEmpty()){
            selectedNoticeRepository.deleteAll(existingList)
        }

        val listToSave = NoticeModelToEntity.SelectedNoticeModelsToEntities.from(selectedUnpaidNoticeList ?: emptyList())
        listToSave.forEach {
            it.userLogin = userName
        }
        if (listToSave.isNotEmpty()){
            val savedList = selectedNoticeRepository.saveAll(listToSave)
        }
    }

}