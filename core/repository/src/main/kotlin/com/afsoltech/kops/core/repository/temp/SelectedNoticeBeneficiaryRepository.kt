package com.afsoltech.kops.core.repository.temp

import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.repository.base.BaseRepository
import com.afsoltech.kops.core.entity.customs.temp.SelectedNoticeBeneficiary

interface SelectedNoticeBeneficiaryRepository: BaseRepository<SelectedNoticeBeneficiary, Long> {
    fun findByStatus(status: BaseStatus) : List<SelectedNoticeBeneficiary>
}