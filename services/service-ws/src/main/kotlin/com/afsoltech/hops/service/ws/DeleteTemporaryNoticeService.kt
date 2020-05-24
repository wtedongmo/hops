package com.afsoltech.hops.service.ws

import com.afsoltech.core.service.utils.LoadSettingDataToMap
import com.afsoltech.hops.core.entity.customs.temp.SelectedNotice
import com.afsoltech.hops.core.repository.temp.SelectedNoticeBeneficiaryRepository
import com.afsoltech.hops.core.repository.temp.SelectedNoticeRepository
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class DeleteTemporaryNoticeService (val selectedNoticeRepository: SelectedNoticeRepository,
                                    val selectedNoticeBenefRepository: SelectedNoticeBeneficiaryRepository) {

    companion object : KLogging()

//    @Value("\${app.notice.expired.duration.selected}")
    private var expiredSelectedDuration: Long=10

//    @Value("\${sql.request.reset.sequence.temprary.notice}")
    private var requestResetSelectedNotice: String=""

//    @Value("\${sql.request.reset.sequence.temprary.notice.beneficiary}")
    private var requestResetSelectedNoticeBeneficiary: String=""

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate


//    init{
//        expiredSelectedDuration = LoadBaseDataToMap.settingMap.get("app.notice.expired.duration.selected")?.value?.toLong()?: 10
//        requestResetSelectedNotice = LoadBaseDataToMap.settingMap.get("sql.request.reset.sequence.temprary.notice")?.value?: ""
//        requestResetSelectedNoticeBeneficiary = LoadBaseDataToMap.settingMap.get("sql.request.reset.sequence.temprary.notice.beneficiary")?.value?: ""
//    }


    fun deleteTemporarySelectedNotice(){

        expiredSelectedDuration = LoadSettingDataToMap.settingMap.get("app.notice.expired.duration.selected")?.value?.toLong()?: 10
        val currentTime = LocalDateTime.now()
        val expiredTime = currentTime.minusSeconds(expiredSelectedDuration)
        val expiredTemporaryNoticeList = selectedNoticeRepository.findListExpiredNotice(expiredTime)
        logger.trace{"Delete of checked Notices \n $expiredTemporaryNoticeList"}
        if(expiredTemporaryNoticeList.isNotEmpty())
            selectedNoticeRepository.deleteAll(expiredTemporaryNoticeList)
    }

    fun deleteSelectedNoticeAfterPayment(noticeList: List<String>){
        val delNoticeList = selectedNoticeRepository.findListNoticeNumber(noticeList)
//        logger.trace{"Delete of checked Notices after payment \n $delNoticeList"}
        if(delNoticeList.isNotEmpty())
            selectedNoticeRepository.deleteAll(delNoticeList)
    }

    fun deleteSelectedNoticeAfterPayment2(selectedNoticeList: List<SelectedNotice>){
//        val delNoticeList = selectedNoticeRepository.findListNoticeNumber(noticeList)
//        logger.trace{"Delete of checked Notices after payment \n $selectedNoticeList"}
        if(selectedNoticeList.isNotEmpty())
            selectedNoticeRepository.deleteAll(selectedNoticeList)
    }

    @Transactional
    fun resetTemporaryUnpaidNoticeTable(){

        requestResetSelectedNotice = LoadSettingDataToMap.settingMap.get("sql.request.reset.sequence.temprary.notice")?.value?: ""
        requestResetSelectedNoticeBeneficiary = LoadSettingDataToMap.settingMap.get("sql.request.reset.sequence.temprary.notice.beneficiary")?.value?: ""

        logger.trace{" Start Reset sequence in temporary customs table \n"}
        deleteTemporarySelectedNotice()
//        deleteTemporaryUnpaidNotice()
        jdbcTemplate.execute(requestResetSelectedNoticeBeneficiary)
        jdbcTemplate.execute(requestResetSelectedNotice)
        logger.trace{" End Reset sequence in temporary customs table \n"}

    }
}