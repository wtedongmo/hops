package com.afsoltech.hops.core.repository.temp

import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.repository.base.BaseRepository
import com.afsoltech.hops.core.entity.customs.Notice
import com.afsoltech.hops.core.entity.customs.temp.SelectedNotice
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface SelectedNoticeRepository: BaseRepository<SelectedNotice, Long> {
    fun findByStatus(status: BaseStatus) : List<SelectedNotice>

    fun findByNoticeNumber(noticeNumber: String) : List<SelectedNotice>
    fun findByUserLogin(userLogin: String) : List<SelectedNotice>

    @Query("select n from SelectedNotice n where n.dateCreated < :expiredTime")
    fun findListExpiredNotice(@Param("expiredTime") expiredTime: LocalDateTime): List<SelectedNotice>

    @Query("select n from SelectedNotice n where n.noticeNumber in :noticeNumberList")
    fun findListNoticeNumber(@Param("noticeNumberList") noticeNumberList: List<String>) : List<SelectedNotice>

}