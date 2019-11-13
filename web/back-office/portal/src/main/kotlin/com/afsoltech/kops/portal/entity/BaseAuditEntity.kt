package com.nanobnk.epayment.portal.entity

import java.time.LocalDateTime
import javax.persistence.Basic
import javax.persistence.Column
import javax.persistence.MappedSuperclass

@MappedSuperclass
open class BaseAuditEntity(

        @Column(name = "date_created")
//        @Basic(optional = false)
        val dateCreated: LocalDateTime = LocalDateTime.now(),

        @Column(name = "created_by")
//        @Basic(optional = false)
        var createdBy: String = "SYSTEM",

        @Column(name = "date_modified")
//        @Basic(optional = false)
        var dateModified: LocalDateTime = LocalDateTime.now(),

        @Column(name = "modified_by")
//        @Basic(optional = false)
        var modifiedBy: String = "SYSTEM"

//        @Column(name = "receipt")
//        var receipt: String? = null,
//
//        @Column(name = "date_completed")
//        @Basic(optional = false)
//        var dateCompleted: LocalDateTime = LocalDateTime.now()

)