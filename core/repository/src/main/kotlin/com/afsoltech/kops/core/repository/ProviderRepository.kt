package com.afsoltech.core.repository

import com.afsoltech.core.repository.base.BaseRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable

interface ProviderRepository : BaseRepository<ProviderEntity, Long> {

    @Cacheable(cacheNames = ["ProviderRepository.findByParticipantCode"])
    fun findByParticipantCode(participantCode : String?) : ProviderEntity?

    @CacheEvict(cacheNames = ["ProviderRepository.findByParticipantCode"], allEntries = true)
    override fun <S : ProviderEntity> save(var1: Iterable<S>): List<S>

    fun findByParticipantStatus(participantStatus : ParticipantStatus?) : List<ProviderEntity>
    fun findByProviderUserId(providerUserId : String?) : ProviderEntity?

    fun findByProviderType(providerType : ProviderType?) : List<ProviderEntity>

}