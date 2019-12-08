package com.afsoltech.kops.service.integration

import com.afsoltech.kops.core.repository.temp.SelectedNoticeBeneficiaryRepository
import com.afsoltech.kops.core.repository.temp.SelectedNoticeRepository
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
class DeleteTemporaryNoticeService (val selectedNoticeRepository: SelectedNoticeRepository,
                                    val selectedNoticeBenefRepository: SelectedNoticeBeneficiaryRepository) {

    companion object : KLogging()

    @Value("\${app.notice.expired.duration.selected}")
    var expiredSelectedDuration: Long=10

    fun deleteTemporarySelectedNotice(){

        val currentTime = LocalDateTime.now()
        val expiredTime = currentTime.minusMinutes(expiredSelectedDuration)
        val expiredTemporaryNoticeList = selectedNoticeRepository.findListExpiredNotice(expiredTime)
        logger.trace{"Delete of checked Notices \n $expiredTemporaryNoticeList"}
        if(expiredTemporaryNoticeList.isNotEmpty())
            selectedNoticeRepository.deleteAll(expiredTemporaryNoticeList)
    }

    fun deleteSelectedNoticeAfterPayment(noticeList: List<String>){
        val delNoticeList = selectedNoticeRepository.findListNoticeNumber(noticeList)
        logger.trace{"Delete of checked Notices after payment \n $delNoticeList"}
        if(delNoticeList.isNotEmpty())
            selectedNoticeRepository.deleteAll(delNoticeList)
    }

    @Value("\${sql.request.reset.sequence.temprary.notice}")
    lateinit var requestResetSelectedNotice: String

    @Value("\${sql.request.reset.sequence.temprary.notice.beneficiary}")
    lateinit var requestResetSelectedNoticeBeneficiary: String

        @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Transactional
    fun resetTemporaryUnpaidNoticeTable(){

        logger.trace{" Start Reset sequence in temporary customs table \n"}
        deleteTemporarySelectedNotice()
//        deleteTemporaryUnpaidNotice()
        jdbcTemplate.execute(requestResetSelectedNoticeBeneficiary)
        jdbcTemplate.execute(requestResetSelectedNotice)
        logger.trace{" End Reset sequence in temporary customs table \n"}

    }
}