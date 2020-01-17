//package com.afsoltech.hops.core.repository
//
//import com.afsoltech.core.repository.base.BaseRepository
//import com.afsoltech.hops.core.entity.customs.NoticeSummary
//import java.math.BigDecimal
//import java.time.LocalDate
//
//interface NoticeSummaryRepository : BaseRepository<NoticeSummary, Long> {
//
//    fun findByPaymentDate(paymentDate: LocalDate?): List<NoticeSummary>
//
//    fun findByPaymentDateAndNumberOfNoticeAndTotalAmount(paymentDate: LocalDate, numberOfNotice: Int, totalAmount: BigDecimal): NoticeSummary?
//
//}