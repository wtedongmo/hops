package com.afsoltech.kops.core.repository

import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.repository.base.BaseRepository
import com.afsoltech.kops.core.entity.customs.Beneficiary

interface BeneficiaryRepository: BaseRepository<Beneficiary, Long> {

    fun findByStatus(status: BaseStatus) : List<Beneficiary>
}