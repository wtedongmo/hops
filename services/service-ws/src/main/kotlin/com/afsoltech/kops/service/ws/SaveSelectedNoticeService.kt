package com.afsoltech.kops.service.ws

import com.afsoltech.kops.core.entity.customs.temp.SelectedNotice
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.core.repository.temp.SelectedNoticeRepository
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
import com.afsoltech.kops.service.integration.RetrieveSelectedUnpaidNoticeService
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SaveSelectedNoticeService(val selectedNoticeRepository: SelectedNoticeRepository,
                                val retrieveSelectedUnpaidNoticeService: RetrieveSelectedUnpaidNoticeService) {

    companion object : KLogging()

    @Transactional
    @Synchronized
    fun saveSelectedNotices(userLogin: String, selectedNoticeList: List<String>) : List<SelectedNotice> { //:Boolean

        val selectedUnpaidNoticeList = mutableListOf<UnpaidNoticeResponseDto>()
        selectedNoticeList.forEach {
            selectedUnpaidNoticeList.add(ListUnpaidNoticeService.unpaidNoticeCache!!.get(it))
        }

        return retrieveSelectedUnpaidNoticeService.updateExistingNotice(selectedUnpaidNoticeList, userLogin)

//        val existingList = selectedNoticeRepository.findByUserLogin(userLogin)
//        if(existingList.isNotEmpty()){
//            selectedNoticeRepository.deleteAll(existingList)
//        }
//
//        val listToSave = NoticeModelToEntity.SelectedNoticeModelsToEntities.from(selectedUnpaidNoticeList ?: emptyList())
//        listToSave.forEach {
//            it.userLogin = userLogin
//        }
//        if (listToSave.isNotEmpty()){
//            val savedList = selectedNoticeRepository.saveAll(listToSave)
//
//        }
    }

}