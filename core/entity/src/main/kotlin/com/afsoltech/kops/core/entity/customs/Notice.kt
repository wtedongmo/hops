package com.afsoltech.kops.core.entity.customs

import com.afsoltech.core.entity.BaseAuditEntity
import com.afsoltech.kops.core.model.attribute.ErrorCode
import com.afsoltech.kops.core.model.integration.PaymentStatus
import com.nanobnk.epayment.model.attribute.VentilationStatus
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.*


@Entity
@Table(name = "NOTICE")
data class Notice(

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id
        @Column(name = "NOTICE_ID")
        var id: Long? = null,

        @Column(name = "NOTICE_NUMBER", columnDefinition = "varchar(100)", unique = true)
        @Basic(optional = false)
        var noticeNumber: String? = null,

        @Column(name = "NOTIFICATION_DATE")
        @Basic(optional = false)
        var notificationDate: LocalDate? = null,

        @Column(name = "NOTICE_TYPE", columnDefinition = "char(1)")
        var noticeType: String? = null,

        @Column(name = "REFERENCE_NUMBER", columnDefinition = "varchar(100)")
        var referenceNumber: String? = null,

        @Column(name = "DECLARATION_TYPE", columnDefinition = "char(3)")
        var declarationType: String? = null,

        @Column(name = "TAXPAYER_NUMBER", columnDefinition = "varchar(100)")
        var taxpayerNumber: String? = null,

        @Column(name = "CUSTOMER_NAME")
        var taxpayerName: String? = null,

        @Column(name = "CDA_NUMBER", columnDefinition = "varchar(100)", nullable = true)
        var cdaNumber: String? = null,

        @Column(name = "CDA_CODE", columnDefinition = "varchar(100)")
        var cdaCode: String? = null,

        @Column(name = "CDA_NAME")
        var cdaName: String? = null,

        @Column(name = "ISSUER_OFFICE", columnDefinition = "char(5)")
        var issuerOffice: String? = null,

        @Column(name = "DUE_DATE")
        var dueDate: LocalDate? = null,

        @Column(name = "AMOUNT")
        var amount: BigDecimal? = null,

        @Column(name = "PAYMENT_DATE")
        var paymentDate: LocalDateTime? = null,

        @Column(name = "PAYMENT_AMOUNT")
        var paymentAmount: BigDecimal? = null,

        @Column(name = "PAYMENT_METHOD", columnDefinition = "varchar(15)")
        var paymentMethod : String? = null,

        @Column(name = "PAYMENT_CATEGORY", columnDefinition = "char(3)")
        var paymentCategory: String? = null,

        @Column(name = "NOTICE_STATUS", columnDefinition = "varchar(20)")
        @Enumerated(value = EnumType.STRING)
        var noticeStatus: PaymentStatus? = PaymentStatus.IN_PROGRESS,


        @Column(name = "USER_NAME")
        var userName: Long?= null,

        @Column(name = "PAYMENT_NUMBER")
        var paymentNumber: String? = null,

        @Column(name = "RECONCILED")
        var isReconciled: Boolean? = null,

        @Column(name = "RECONCILIATION_DATE")
        var reconciliationDate: LocalDateTime? = null,

        @Column(name = "REMOTE_PAIMENT_AMOUNT")
        var remotePaymentAmount: BigDecimal? = null,

        @Column(name = "REMOTE_PAIMENT_DATE")
        var remotePaymentDate: LocalDateTime? = null,

        @Column(name = "REMOTE_PAIMENT_NUMBER")
        var remotePaymentNumber: String? = null,

        @Column(name = "PAYMENT_ID")
        var paymentId: Long? = null,

        @Column(name = "NOTICE_SUMMARY_ID")
        var noticeSummaryId: Long? = null,

        @Column(name = "ventilation_status", columnDefinition = "varchar(15)")
        @Enumerated(value = EnumType.STRING)
        var ventilationStatus: VentilationStatus? = null,

        @Fetch(FetchMode.JOIN)
        @OneToMany(mappedBy = "notice", cascade = [(CascadeType.ALL)])
        var listNoticeBeneficiary: MutableList<NoticeBeneficiary> = ArrayList()


) : BaseAuditEntity()



