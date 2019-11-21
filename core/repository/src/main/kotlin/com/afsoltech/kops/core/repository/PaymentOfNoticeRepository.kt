package com.afsoltech.kops.core.repository

import com.afsoltech.core.repository.base.BaseRepository
import com.afsoltech.kops.core.entity.customs.PaymentOfNotice


interface PaymentOfNoticeRepository : BaseRepository<PaymentOfNotice, Long> {

//    fun findByOutboundNoticeNumber(outboundNoticeNumber: String): List<PaymentOfNotice>?
}