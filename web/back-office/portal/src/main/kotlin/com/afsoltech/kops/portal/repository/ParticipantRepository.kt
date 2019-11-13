package com.nanobnk.epayment.portal.repository

import com.nanobnk.epayment.model.attribute.BankStatus
import com.nanobnk.epayment.portal.entity.Bank
import com.nanobnk.epayment.portal.entity.Participant
import com.nanobnk.util.jpa.repository.BaseRepository

interface ParticipantRepository : BaseRepository<Participant, Long> {
    fun findByParticipantCode(participantCode: String): Participant?
    fun findByParticipantStatusAndPaymentLinkIsNotNull(participantStatus: String): List<Participant>
}

