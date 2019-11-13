//package com.nanobnk.epayment.portal.entity
//
//import com.nanobnk.epayment.model.attribute.OTPStatus
//import com.nanobnk.epayment.model.attribute.RequestTypePortal
//import com.nanobnk.epayment.model.attribute.UserCategory
//import com.nanobnk.epayment.model.attribute.UserPrivilege
//import org.hibernate.annotations.Fetch
//import org.hibernate.annotations.FetchMode
//import java.time.LocalDateTime
//import java.time.LocalTime
//import javax.persistence.*
//
//@Entity
//@Table(name = "SESSION_LOG")
//data class SessionLog(
//        @SequenceGenerator(name = "SESSION_LOG_ID", sequenceName = "SESSION_LOG_ID", allocationSize = 1)
//        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SESSION_LOG_ID")
//        @Id
//        @Column(name = "SESSION_LOG_ID")
//        var sessionLogId: Long? = null,
//
//        @Column(name = "USER_ID")
//        var userId: Long? = null,
//
//        @Column(name = "METHOD", columnDefinition = "varchar(10)")
//        var method: String? = null,
//
//        @Column(name = "REQUEST_URL", columnDefinition = "varchar(100)")
//        var requestUrl: String? = null,
//
//        @Column(name = "AUTH_TYPE", columnDefinition = "varchar(10)")
//        var authType: String? = null,
//
//        @Column(name = "QUERY")
//        var query: String? = null,
//
//        @Column(name = "RESPONSE_STATUS", columnDefinition = "varchar(10)")
//        var responseStatus: String? = null,
//
//        //@Basic(optional = false)
//        @Column(name = "USERNAME", columnDefinition = "varchar(100)")
//        var username: String? = null,
//
//        @Column (name = "REQUEST_TYPE", columnDefinition = "varchar(30)")
//        @Enumerated(value = EnumType.STRING)
//        var requestType: RequestTypePortal? = null,
//
//        @Column(name = "HOST", columnDefinition = "varchar(30)")
//        var host: String? = null,
//
//        //@Basic(optional = false)
//        @Column(name = "REQUEST", columnDefinition = "text")
//        var request: String? = null,
//
//        //@Basic(optional = false)
//        @Column(name = "RESPONSE", columnDefinition = "text")
//        var response: String? = null,
//
//        @Column(name = "START_TIME")
//        var startTime: LocalTime? = null,
//
//        @Column(name = "END_TIME")
//        var endTime: LocalTime? = null,
//
//        @Column(name = "DURATION")
//        var duration: Long? = null,
//
//        @Column(name = "SESSION_MODULE", columnDefinition = "varchar(15)")
//        var ssessionModule: String? = "PORTAL"
////
////        @Column(name = "MODIFIED_DATE")
////        var modifiedDate: LocalDateTime = LocalDateTime.now()
//
////        @Column(name = "LOGIN_ATTEMPTS")
////        var loginAttempts: Int? = null,
////
////        @Column(name = "LAST_LOGIN_ATTEMPT")
////        var lastLoginAttempt: LocalDateTime? = null
//
//) : BaseAuditEntity()
