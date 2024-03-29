package com.afsoltech.hops.core.repository

import com.afsoltech.core.repository.base.BaseRepository
import com.afsoltech.hops.core.entity.customs.NoticeBeneficiary
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface NoticeBeneficiaryRepository : BaseRepository<NoticeBeneficiary, Long> {

    @Query("select n from NoticeBeneficiary n where n.notice.id = :noticeId")
    fun findByNoticeId(@Param("noticeId") noticeId: Long) : List<NoticeBeneficiary>

    @Query("select n from NoticeBeneficiary n where n.notice.noticeNumber = :noticeNumber")
    fun findByNoticeNumber(@Param("noticeNumber") noticeNumber: String) : List<NoticeBeneficiary>

    @Query("select n from NoticeBeneficiary n where n.notice.noticeNumber in :noticeNumberList")
    fun findByListNoticeNumber(@Param("noticeNumberList") noticeNumberList: List<String>) : List<NoticeBeneficiary>

    @Query("select n from NoticeBeneficiary n where n.notice.noticeStatus ='COMPLETED' " +
            "and n.notice.noticeNumber in :noticeNumberList")
    fun findByListNoticesNumberCompleted(@Param("noticeNumberList") noticeNumberList: List<String>): List<NoticeBeneficiary>

    fun findByBeneficiaryCode(beneficiaryCode: String) : List<NoticeBeneficiary>

}