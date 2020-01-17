package com.afsoltech.hops.core.repository.temp

import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.repository.base.BaseRepository
import com.afsoltech.hops.core.entity.customs.temp.SelectedNoticeBeneficiary
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface SelectedNoticeBeneficiaryRepository: BaseRepository<SelectedNoticeBeneficiary, Long> {
    fun findByStatus(status: BaseStatus) : List<SelectedNoticeBeneficiary>

    @Query("select n from SelectedNoticeBeneficiary n where n.selectedNotice.id in :noticeIdList")
    fun findBySelectedNoticeList(@Param("noticeIdList") noticeIdList: List<Long>) : List<SelectedNoticeBeneficiary>
}