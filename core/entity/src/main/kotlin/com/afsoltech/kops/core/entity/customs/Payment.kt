package com.nanobnk.epayment.entity

import com.afsoltech.core.entity.BaseAuditEntity
import com.afsoltech.kops.core.entity.customs.PaymentOfNotice
import com.afsoltech.kops.core.model.integration.PaymentStatus
import com.nanobnk.epayment.model.attribute.VentilationStatus
import org.hibernate.annotations.Fetch
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "PAYMENT")
@Entity
data class Payment (

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id
        @Column(name = "PAYMENT_ID")
        var id: Long? = null,

        @Column(name = "PAYMENT_NUMBER", columnDefinition = "varchar(100)", unique = true)
        @Basic(optional = false)
        var paymentNumber : String? = null,

        @Column(name = "BANK_PAYMENT_NUMBER", columnDefinition = "varchar(100)", unique = true)
        @Basic(optional = false)
        var bankPaymentNumber : String? = null,

        @Column(name = "BANK_CODE", columnDefinition = "varchar(10)")
        var bankCode : String? = null,

        @Column(name = "BANK_AGENCY_CODE", columnDefinition = "varchar(10)")
        var bankAgencyCode : String? = null,

        @Column(name = "USER_NAME", columnDefinition = "varchar(50)")
        var userName : String? = null,

        @Column(name = "TAXPAYER_NUMBER", columnDefinition = "varchar(100)")
        @Basic(optional = false)
        var taxpayerNumber : String? = null,

        @Column(name = "PAYER_ACCOUNT_NUMBER", columnDefinition = "varchar(100)")
        var payerAccountNumber : String? = null,

        @Column(name = "PAYER_ACCOUNT_NAME", columnDefinition = "varchar(100)")
        var payerAccountName : String? = null,

        @Column(name = "PAYMENT_METHOD", columnDefinition = "varchar(15)")
        var paymentMethod : String? = null,

        @Column(name = "TOTAL_AMOUNT")
        @Basic(optional = false)
        var totalAmount : BigDecimal? = null,

        @Basic(optional = false)
        @Column(name = "PAYMENT_DATE")
        var paymentDate: LocalDateTime? = null,


        @Column(name = "REMOTE_PAYMENT_NUMBER")
        var outboundPaymentNumber : Long? = null,

        @Column(name = "PAYMENT_STATUS", columnDefinition = "varchar(15)")
        @Enumerated(value = EnumType.STRING)
        var paymentStatus: PaymentStatus? = PaymentStatus.IN_PROGRESS,


        @Column(name = "ventilation_status", columnDefinition = "varchar(15)")
        @Enumerated(value = EnumType.STRING)
        var ventilationStatus: VentilationStatus? = null,

        @Column(name = "ventilation_message", columnDefinition="TEXT")
        var ventilationMessage: String?=null,

        @Fetch(org.hibernate.annotations.FetchMode.JOIN)
        @OneToMany(mappedBy = "payment", cascade = [(CascadeType.ALL)])
        var noticeList: MutableList<PaymentOfNotice> = ArrayList()

): BaseAuditEntity()