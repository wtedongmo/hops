package com.afsoltech.hops.core.repository

import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.repository.base.BaseRepository
import com.afsoltech.hops.core.entity.customs.Beneficiary

interface BeneficiaryRepository: BaseRepository<Beneficiary, Long> {

//    fun findByStatus(status: BaseStatus) : List<Beneficiary>
}