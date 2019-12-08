package com.afsoltech.kops.core.entity.customs

import com.afsoltech.core.entity.BaseAudit
import com.afsoltech.core.model.attribute.BaseStatus
import javax.persistence.*

@Entity
@Table(name = "BENEFICIARY")
data class Beneficiary (

        @SequenceGenerator(name = "BENEFICIARY_ID", sequenceName = "BENEFICIARY_ID", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BENEFICIARY_ID")
        @Id
        @Column(name = "BENEFICIARY_ID")
        var id: Long? = null,

        @Basic(optional = false)
        @Column(name = "CODE", columnDefinition = "char(5)", unique = true)
        var code: String? = null,

        @Column(name = "NAME", columnDefinition = "varchar(100)")
        @Basic(optional = false)
        var  name: String? = null

): BaseAudit()