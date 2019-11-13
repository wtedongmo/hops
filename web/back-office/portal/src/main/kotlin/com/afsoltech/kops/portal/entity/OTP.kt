package com.nanobnk.epayment.portal.entity

import com.nanobnk.epayment.model.attribute.OTPStatus
import java.time.LocalDateTime
import javax.persistence.*

@Entity
@Table(name = "OTP")
data class OTP(
        @SequenceGenerator(name = "OTP_ID", sequenceName = "OTP_ID", allocationSize = 1)
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OTP_ID")
        @Id
        @Column(name = "OTP_ID")
        var OTPId: Long? = null,

        @Column(name = "OPT_CODE", columnDefinition = "varchar(50)")
        var otpCode: String? = null,

        @Column(name = "OTP_STATUS", columnDefinition = "varchar(50)")
        @Enumerated(value = EnumType.STRING)
        var otpStatus: OTPStatus? = null,

        @Column(name = "OTP_EXPIRED_TIME")
        var otpExpiredTime: LocalDateTime? = null,

        @Column(name = "CREATED_DATE")
        var createdDate: LocalDateTime? = null,

        @JoinColumn(name = "APP_USER_ID", referencedColumnName = "APP_USER_ID")
        @ManyToOne(optional = false)
        var appUser: AppUser?=null

        )
