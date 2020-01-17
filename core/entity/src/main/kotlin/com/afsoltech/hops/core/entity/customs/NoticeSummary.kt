//package com.afsoltech.hops.core.entity.customs
//
//import com.afsoltech.core.entity.BaseAudit
//import java.math.BigDecimal
//import java.time.LocalDate
//import javax.persistence.*
//
//@Table(name = "NOTICE_SUMMARY")
//@Entity
//data class NoticeSummary (
////        @GeneratedValue(strategy = GenerationType.IDENTITY)
////        @SequenceGenerator(name = "NOTICE_SUMMARY_ID", sequenceName = "NOTICE_SUMMARY_ID", allocationSize = 1)
////        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTICE_SUMMARY_ID")
//        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
//        @SequenceGenerator(name = "sequenceGenerator")
//        @Id
//        @Column(name = "NOTICE_SUMMARY_ID")
//        var id: Long? = null,
//
//        @Column(name = "PAYMENT_DATE")
//        @Basic(optional = false)
//        var paymentDate: LocalDate? = null,
//
//        @Column(name = "NUMBER_OF_NOTICE")
//        var numberOfNotice: Int? = null,
//
//        @Column(name = "TOTAL_AMOUNT")
//        var totalAmount: BigDecimal? = null,
//
//        @Column(name = "SUMMARY_IDENTIFIER", columnDefinition = "varchar(50)")
//        var summaryIdentifier: String? = null
//
//): BaseAudit()