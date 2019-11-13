package com.afsoltech.core.repository

import com.afsoltech.core.repository.base.BaseRepository

interface ParticipantViewRepository : BaseRepository<ParticipantView, Long> {
    fun findByParticipantCode(participantCode: String): ParticipantView?
    fun findByParticipantStatusAndPaymentLinkIsNotNull(participantStatus: String): List<ParticipantView>
}

