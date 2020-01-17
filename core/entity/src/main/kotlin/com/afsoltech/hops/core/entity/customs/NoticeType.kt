package com.afsoltech.hops.core.entity.customs

import com.afsoltech.core.entity.BaseAudit
import javax.persistence.*

@Entity
@Table(name = "NOTICE_TYPE")
data class NoticeType (

//        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @SequenceGenerator(name = "NOTICE_TYPE_ID", sequenceName = "NOTICE_TYPE_ID", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "NOTICE_TYPE_ID")
        @Id
        @Column(name = "NOTICE_TYPE_ID")
        var id: Long? = null,

        @Basic(optional = false)
        @Column(name = "CODE", columnDefinition = "char(1)", unique = true)
        var code: String? = null,

        @Column(name = "NAME", columnDefinition = "varchar(100)")
        @Basic(optional = false)
        var name: String? = null

): BaseAudit()