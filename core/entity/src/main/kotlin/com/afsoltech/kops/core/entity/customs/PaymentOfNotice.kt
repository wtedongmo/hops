package com.afsoltech.kops.core.entity.customs

import com.afsoltech.core.entity.BaseAuditEntity
import com.nanobnk.epayment.entity.Payment
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "PAYMENT_OF_NOTICE")
@Entity
data class PaymentOfNotice (

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id
        @Column(name = "PAYMENT_OF_NOTICE_ID")
        var id: Long? = null,

        @Column(name = "AMOUNT")
        var amount : BigDecimal? = null,

        @Column(name = "NOTICE_ID")
        var noticeId : Long? = null,

        @Basic(optional = false)
        @JoinColumn(name = "PAYMENT_ID", referencedColumnName = "PAYMENT_ID")
        @ManyToOne (fetch = FetchType.LAZY)
        var payment: Payment? = null
): BaseAuditEntity()