package com.nanobnk.epayment.portal.repository

import com.nanobnk.epayment.model.attribute.OTPStatus
import com.nanobnk.epayment.portal.entity.OTP
import com.nanobnk.util.jpa.repository.BaseRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface OtpRepository : BaseRepository<OTP, Long> {

    @Query("select o from OTP o where ((o.appUser.nui= :nui or o.appUser.email= :email) and o.otpStatus in :statusList" + //
                                        " and o.otpExpiredTime>= :currentTime)") //AndOtpStatus
    fun findByAppUserNuiOrEmailAndOtpExpiredTimeGreatThanNow(@Param("nui") nui: String, @Param("email") email: String,
          @Param("statusList") statusList: List<OTPStatus>, @Param("currentTime") currentTime: LocalDateTime= LocalDateTime.now()): List<OTP>
    //

    fun findByAppUser_UserIdAndOtpStatusAndOtpExpiredTimeGreaterThanEqual(appUserId: Long, status: OTPStatus, currentDate: LocalDateTime = LocalDateTime.now()): List<OTP>

}

