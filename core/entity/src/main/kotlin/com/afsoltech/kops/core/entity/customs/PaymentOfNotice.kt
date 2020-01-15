package com.afsoltech.kops.core.entity.customs

import com.afsoltech.core.entity.BaseAudit
import com.afsoltech.core.entity.cap.Payment
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "PAYMENT_OF_NOTICE")
@Entity
data class PaymentOfNotice (

//        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @SequenceGenerator(name = "PAYMENT_OF_NOTICE_ID", sequenceName = "PAYMENT_OF_NOTICE_ID", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PAYMENT_OF_NOTICE_ID")
        @Id
        @Column(name = "PAYMENT_OF_NOTICE_ID")
        var id: Long? = null,

        @Column(name = "NOTICE_NUMBER")
        var noticeNumber : String? = null,

        @Column(name = "AMOUNT")
        var amount : BigDecimal? = null,

        @Column(name = "NOTICE_ID")
        var noticeId : Long? = null,

//        @Basic(optional = false)
//        @JoinColumn(name = "NOTICE_ID", referencedColumnName = "NOTICE_ID")
//        @ManyToOne (fetch = FetchType.LAZY)
//        var notice: Notice? = null,

        @Basic(optional = false)
        @JoinColumn(name = "PAYMENT_ID", referencedColumnName = "PAYMENT_ID")
        @ManyToOne (fetch = FetchType.LAZY)
        var payment: Payment? = null

): BaseAudit()