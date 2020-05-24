package com.afsoltech.hops.core.entity.customs.temp

import com.afsoltech.core.entity.BaseAudit
import java.math.BigDecimal
import javax.persistence.*

@Table(name = "SELECTED_NOTICE_BENEFICIARY")
@Entity
data class SelectedNoticeBeneficiary (

//        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @SequenceGenerator(name = "SELECTED_NOTICE_BENEFICIARY_ID", sequenceName = "SELECTED_NOTICE_BENEFICIARY_ID", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SELECTED_NOTICE_BENEFICIARY_ID")
        @Id
        @Column(name = "SELECTED_NOTICE_BENEFICIARY_ID")
        var id: Long?? = null,

        @Column(name = "NAME", columnDefinition = "varchar(150)")
        @Basic(optional = false)
        var name : String? = null,

        @Column(name = "CODE", columnDefinition = "varchar(10)")
        @Basic(optional = false)
        var code : String? = null,

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

): BaseAudit()