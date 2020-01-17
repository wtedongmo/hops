package com.afsoltech.hops.core.entity.customs

import com.afsoltech.core.entity.BaseAudit
import com.afsoltech.core.model.attribute.VentilationStatus
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "NOTICE_BENEFICIARY")
@Entity
data class NoticeBeneficiary (

        @SequenceGenerator(name = "NOTICE_BENEFICIARY_ID", sequenceName = "NOTICE_BENEFICIARY_ID", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTICE_BENEFICIARY_ID")
        @Id
        @Column(name = "NOTICE_BENEFICIARY_ID")
        var id: Long? = null,

        @Column(name = "BENEFICIARY_NAME", columnDefinition = "varchar(150)")
        @Basic(optional = false)
        var beneficiaryName : String? = null,

        @Column(name = "BENEFICIARY_CODE", columnDefinition = "varchar(10)")
        @Basic(optional = false)
        var beneficiaryCode : String? = null,

        @Column(name = "BANK_CODE", columnDefinition = "varchar(10)")
        @Basic(optional = false)
        var bankCode : String? = null,

        @Column(name = "ACCOUNT_NUMBER", columnDefinition = "varchar(30)")
        @Basic(optional = false)
        var accountNumber : String? = null,

        @Basic(optional = false)
        @Column(name = "AMOUNT")
        var amount: BigDecimal? = null,


        @Column(name = "ventilation_status", columnDefinition = "varchar(15)")
        @Enumerated(value = EnumType.STRING)
        var ventilationStatus: VentilationStatus? = null,

        @Column(name = "BENEFICIARY_ID")
        var beneficiaryId: Long? = null,

        @Basic(optional = false)
        @JoinColumn(name = "NOTICE_ID", referencedColumnName = "NOTICE_ID")
        @ManyToOne(fetch = FetchType.LAZY)
        var notice: Notice? = null

): BaseAudit()