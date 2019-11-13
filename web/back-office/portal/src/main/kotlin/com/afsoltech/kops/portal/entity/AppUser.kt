package com.nanobnk.epayment.portal.entity

import com.nanobnk.epayment.model.attribute.UserCategory
import com.nanobnk.epayment.model.attribute.UserPrivilege
import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "APP_USER")
data class AppUser(
        @SequenceGenerator(name = "APP_USER_ID", sequenceName = "APP_USER_ID", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APP_USER_ID")
        @Id
        @Column(name = "APP_USER_ID")
        var userId: Long? = null,

        @Basic(optional = false)
        @Column(name = "USERNAME", unique = true, columnDefinition = "varchar(70)")
        var username: String? = null,

        @Basic(optional = false)
        @Column(name = "PASSWORD")
        var password: String? = null,

        @Basic(optional = false)
        @Column(name = "NUI", columnDefinition = "varchar(20)")
        var nui: String? = null,

        @Basic(optional = false)
        @Column(name = "EMAIL", columnDefinition = "varchar(50)")
        var email: String? = null,

        @Basic(optional = false)
        @Column(name = "CATEGORY", columnDefinition = "varchar(4)")
        @Enumerated(value = EnumType.STRING)
        var category: UserCategory? = null,

        @Basic(optional = false)
        @Column(name = "PRIVILEGE", columnDefinition = "varchar(15)")
        @Enumerated(value = EnumType.STRING)
        var privilege: UserPrivilege? = null

//        @Column(name = "CREATED_DATE")
//        var createdDate: LocalDateTime = LocalDateTime.now()

//        @Column(name = "LOGIN_ATTEMPTS")
//        var loginAttempts: Int? = null,
//
//        @Column(name = "LAST_LOGIN_ATTEMPT")
//        var lastLoginAttempt: LocalDateTime? = null

) : BaseAuditEntity()
