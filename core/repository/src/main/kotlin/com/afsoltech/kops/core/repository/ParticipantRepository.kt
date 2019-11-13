package com.afsoltech.core.repository

import com.afsoltech.core.repository.base.BaseRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable

interface ParticipantRepository : BaseRepository<ParticipantEntity, Long> {

    @Cacheable(cacheNames = ["articipantRepository.findByParticipantCode"])
    fun findByParticipantCode(participantCode: String?): ParticipantEntity?

    @CacheEvict(cacheNames = ["ParticipantRepository.findByParticipantCode"], allEntries = true)
    override fun <S : ParticipantEntity> save(var1: Iterable<S>): List<S>

    @Cacheable(cacheNames = ["ParticipantRepository.findByInboundParticipantApiKeyValue"])
    fun findByParticipantApiKeyValue(participantApiKeyValue: String?): ParticipantEntity?

}