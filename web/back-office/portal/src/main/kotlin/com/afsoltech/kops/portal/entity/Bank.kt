package com.nanobnk.epayment.portal.entity

import com.nanobnk.epayment.model.attribute.BankStatus
import javax.persistence.*

//@Entity
//@Table(name = "BANK")
data class Bank(
//        @SequenceGenerator(name = "BANK_ID", sequenceName = "BANK_ID", allocationSize = 1)
//        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BANK_ID")
        @Id
        @Column(name = "BANK_ID")
        var userId: Long? = null,

//        @Basic(optional = false)
        @Column(name = "BANK_CODE", columnDefinition = "varchar(10)") // unique = true,
        var bankCode: String? = null,

        @Basic(optional = false)
        @Column(name = "BANK_NAME", columnDefinition = "varchar(70)")
        var bankName: String? = null,

//        @Basic(optional = false)
        @Column(name = "BANK_ABREVIATION", columnDefinition = "varchar(20)")
        var bankAbreviation: String? = null,

        @Basic(optional = false)
        @Column(name = "BANK_LINK")
        var bankLink: String? = null,

        @Column(name = "BANK_STATUS", columnDefinition = "varchar(15)")
        @Enumerated(value = EnumType.STRING)
        var bankStatus: BankStatus = BankStatus.CREATED

): BaseAuditEntity()