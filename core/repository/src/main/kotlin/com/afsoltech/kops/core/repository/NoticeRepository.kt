package com.afsoltech.kops.core.repository

import com.afsoltech.core.repository.base.BaseRepository
import com.afsoltech.kops.core.entity.customs.Notice
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate


interface NoticeRepository : BaseRepository<Notice, Long> {

    fun findByNoticeNumber(noticeNumber: String) : List<Notice>

    @Query("select n from Notice n where n.noticeNumber = :noticeNumber and n.noticeStatus ='COMPLETED'")
    fun findByNoticeNumberCompleted(@Param("noticeNumber") noticeNumber: String) : List<Notice>

    @Query("select n from Notice n where n.noticeNumber in :noticeNumberList")
    fun findByListNoticeNumber(@Param("noticeNumberList") noticeNumberList: List<String>) : List<Notice>

    @Query("select n from Notice n where n.noticeStatus ='COMPLETED' and n.noticeNumber in :noticeNumberList")
    fun findByListNoticesNumberCompleted(@Param("noticeNumberList") noticeNumberList: List<String>) : List<Notice>

    fun findByDeclarationType(declarationType: String) : List<Notice>

    fun findByNoticeType(noticeType: String) : List<Notice>

    fun findByPaymentCategory(paymentCategory: String) : List<Notice>

    fun findByPaymentMethod(paymentMethod: String) : List<Notice>

    fun findByIssuerOffice(issuerOffice: String) : List<Notice>
}
