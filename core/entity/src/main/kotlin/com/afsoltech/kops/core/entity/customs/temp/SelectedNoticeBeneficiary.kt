package com.afsoltech.kops.core.entity.customs.temp

import com.afsoltech.core.entity.BaseAuditEntity
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "SELECTED_NOTICE_BENEFICIARY")
@Entity
data class SelectedNoticeBeneficiary (

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id
        @Column(name = "SELECTED_NOTICE_BENEFICIARY_ID")
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


        @Basic(optional = false)
        @JoinColumn(name = "SELECTED_NOTICE_ID", referencedColumnName = "SELECTED_NOTICE_ID")
        @ManyToOne(fetch = FetchType.LAZY)
        var selectedNotice: SelectedNotice? = null

): BaseAuditEntity()