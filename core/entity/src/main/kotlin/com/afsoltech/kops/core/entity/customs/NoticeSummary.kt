package com.afsoltech.kops.core.entity.customs

import com.afsoltech.core.entity.BaseAudit
import java.math.BigDecimal
import java.time.LocalDate
import javax.persistence.*

@Table(name = "NOTICE_SUMMARY")
@Entity
data class NoticeSummary (
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id
        @Column(name = "NOTICE_SUMMARY_ID")
        var id: Long? = null,

        @Column(name = "PAYMENT_DATE")
        @Basic(optional = false)
        var paymentDate: LocalDate? = null,

        @Column(name = "NUMBER_OF_NOTICE")
        var numberOfNotice: Int? = null,

        @Column(name = "TOTAL_AMOUNT")
        var totalAmount: BigDecimal? = null,

        @Column(name = "SUMMARY_IDENTIFIER", columnDefinition = "varchar(50)")
        var summaryIdentifier: String? = null

): BaseAudit()