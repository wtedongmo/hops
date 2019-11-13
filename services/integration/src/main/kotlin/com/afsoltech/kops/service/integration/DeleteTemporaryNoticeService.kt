package com.nanobnk.epayment.service

import com.nanobnk.epayment.entity.OutboundNoticePaymentBeneficiaryEntity
import com.nanobnk.epayment.repository.OutboundNoticePaymentBeneficiaryRepository
import com.nanobnk.epayment.repository.OutboundNoticeRepository
import com.nanobnk.epayment.repository.temporary.CheckedNoticeRepository
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
class DeleteTemporaryNoticeService (val outboundNoticeRepository: OutboundNoticeRepository,
                                    val outboundNoticeBenefRepository: OutboundNoticePaymentBeneficiaryRepository,
                                    val checkedNoticeRepository: CheckedNoticeRepository) {

    companion object : KLogging()

    @Value("\${customs.checked.expired.duration}")
    var expiredCheckedDuration: Long=10

    fun deleteTemporaryCheckedNotice(){

        val currentTime = LocalDateTime.now()
        val expiredTime = currentTime.minusMinutes(expiredCheckedDuration)
        val expiredTemporaryNoticeList = checkedNoticeRepository.findListExpiredNotice(expiredTime)
        logger.trace{"Delete of checked Notices \n $expiredTemporaryNoticeList"}
        if(expiredTemporaryNoticeList.isNotEmpty())
            checkedNoticeRepository.delete(expiredTemporaryNoticeList)
    }

    fun deleteCheckedNoticeAfterPayment(noticeList: List<String>){
        val delNoticeList = checkedNoticeRepository.findListNoticeNumber(noticeList)
        logger.trace{"Delete of checked Notices after payment \n $delNoticeList"}
        if(delNoticeList.isNotEmpty())
            checkedNoticeRepository.delete(delNoticeList)
    }

    @Value("\${sql.request.reset.sequence.temprary.customs}")
    lateinit var requestResetNotice: String

    @Value("\${sql.request.reset.sequence.temprary.customs.beneficiary}")
    lateinit var requestResetNoticeBeneficiary: String

    @Value("\${sql.request.reset.sequence.temprary.customs.checked}")
    lateinit var requestResetNoticeChecked: String

    @Autowired
    lateinit var jdbcTemplate: JdbcTemplate

    @Transactional
    fun resetTemporaryUnpaidNoticeTable(){

        logger.trace{" Start Reset sequence in temporary customs table \n"}
        deleteTemporaryCheckedNotice()
        jdbcTemplate.execute(requestResetNoticeChecked)

        deleteTemporaryUnpaidNotice()
        jdbcTemplate.execute(requestResetNoticeBeneficiary)
        jdbcTemplate.execute(requestResetNotice)
        logger.trace{" End Reset sequence in temporary customs table \n"}

    }
}