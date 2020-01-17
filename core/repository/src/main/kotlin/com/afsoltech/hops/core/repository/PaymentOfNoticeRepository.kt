package com.afsoltech.hops.core.repository

import com.afsoltech.core.repository.base.BaseRepository
import com.afsoltech.hops.core.entity.customs.PaymentOfNotice


interface PaymentOfNoticeRepository : BaseRepository<PaymentOfNotice, Long> {

//    fun findByOutboundNoticeNumber(outboundNoticeNumber: String): List<PaymentOfNotice>?
}