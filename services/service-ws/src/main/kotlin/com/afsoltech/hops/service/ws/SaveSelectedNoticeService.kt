package com.afsoltech.hops.service.ws

import com.afsoltech.hops.core.entity.customs.temp.SelectedNotice
import com.afsoltech.hops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.hops.core.repository.temp.SelectedNoticeRepository
import com.afsoltech.hops.service.integration.ListUnpaidNoticeService
import com.afsoltech.hops.service.integration.RetrieveSelectedUnpaidNoticeService
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