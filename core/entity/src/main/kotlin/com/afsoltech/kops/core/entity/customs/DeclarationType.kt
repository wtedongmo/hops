package com.afsoltech.kops.core.entity.customs

import com.afsoltech.core.entity.BaseAudit
import javax.persistence.*

@Entity
@Table(name = "DECLARATION_TYPE")
data class DeclarationType (

        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Id @Column(name = "DECLARATION_TYPE_ID")
        var id: Long? = null,

        @Basic(optional = false)
        @Column(name = "CODE", columnDefinition = "char(3)", unique = true)
        var code: String? = null,

        @Column(name = "NAME", columnDefinition = "varchar(100)")
        @Basic(optional = false)
        var name: String? = null

): BaseAudit()