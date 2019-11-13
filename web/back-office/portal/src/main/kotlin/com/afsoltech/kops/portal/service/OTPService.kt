package com.nanobnk.epayment.portal.service

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.nanobnk.epayment.model.attribute.OTPStatus
import com.nanobnk.epayment.portal.controller.OtpController
import com.nanobnk.epayment.portal.entity.AppUser
import com.nanobnk.epayment.portal.entity.OTP
import com.nanobnk.epayment.portal.repository.AppUserRepository
import com.nanobnk.epayment.portal.repository.OtpRepository
import com.nanobnk.epayment.portal.utils.EmailTemplate
import com.nanobnk.util.rest.error.ExpiredException
import com.nanobnk.util.rest.error.NotFoundException
import mu.KLogging
import org.jasypt.util.password.PasswordEncryptor
import org.jasypt.util.password.rfc2307.RFC2307SSHAPasswordEncryptor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.HashMap

import java.util.Random
import java.util.concurrent.TimeUnit

@Service
class OTPService(val appUserRepository: AppUserRepository, val otpRepository: OtpRepository) {
    companion object : KLogging()

    @Value("\${epayment.portal.otp.expiry.duration.minute:10}")
    var expiryMinutes: Long=10

    @Value("\${epayment.portal.otp.request.limit:5}")
    var otpRequestLimit: Int = 5

    @Value("\${epayment.portal.email.send:true}")
    var boolSendEmail: Boolean = true

    @Value("\${epayment.portal.email.subject}")
    lateinit var emailSubject: String

    @Autowired
    lateinit var myEmailService: EmailService

    private lateinit var encryptor: PasswordEncryptor

    private val otpCache: LoadingCache<String, Int>

    init {
        otpCache = CacheBuilder.newBuilder().expireAfterWrite(expiryMinutes, TimeUnit.MINUTES).build(object : CacheLoader<String, Int>() {
            override fun load(key: String): Int? {
                return 0
            }
        })

        encryptor = RFC2307SSHAPasswordEncryptor()
    }

    fun generateOTPAndSendMail(user: AppUser, resend: Boolean){

        val otp = generateOTP(user, resend)

        logger.info("OTP : $otp")

        //Generate The Template to send OTP
        val template = EmailTemplate("SendOtp.html")

        val replacements = HashMap<String, String>()
        replacements["portal"] = user.username!!
        replacements["otpnum"] = otp.toString()

        val message = template.getTemplate(replacements)
        //val portal = userRepository.findByUsername(username)
        if(boolSendEmail)
            myEmailService.sendOtpMessage(user.email!!, emailSubject, "$message  $otp")

    }
    //This method is used to push the opt number against Key. Rewrite the OTP if it exists
    //Using portal id  as key
    fun generateOTP(user: AppUser, resend: Boolean): Int {

        val userName = user.username!!
//        val portal = appUserRepository.findByUsername(userName)
        user?.let {
            val statusList = listOf<OTPStatus>(OTPStatus.CANCELED, OTPStatus.CREATED)
            val otpList = otpRepository.findByAppUserNuiOrEmailAndOtpExpiredTimeGreatThanNow(user.nui!!, user.email!!, statusList) //OtpStatusAnd
            if (otpList.size>= otpRequestLimit)
                throw ExpiredException("Daily.Retry.OTP.Limit.Exceeded", listOf(otpRequestLimit))

            // If resend, invalidate existing and update OTP Status
            resend?.let {
                otpCache.invalidate(userName)
                val otpSet = otpList.map { item ->
                    item.otpStatus = OTPStatus.CANCELED
                    item
                }
                otpRepository.save(otpSet)
            }
        }

        // Generate OTP and send
        val random = Random()
        val otpCode = 100000 + random.nextInt(900000)

        otpCache.put(userName, otpCode)
        user?.let {
            val otp = OTP(otpCode = encryptor.encryptPassword("$otpCode"), otpStatus = OTPStatus.CREATED,
                    otpExpiredTime = LocalDateTime.now().plusMinutes(expiryMinutes), createdDate = LocalDateTime.now(), appUser = user)
            otpRepository.save(otp)
            return otpCode
        }
        throw NotFoundException("User.Not.Found", listOf(userName.split("#")))
    }

    //This method is used to return the OPT number against Key->Key values is username
    fun getOtp(username: String): Int {
        try {
            return otpCache.get(username)
        } catch (e: Exception) {
            return 0
        }

    }

    //This method is used to clear the OTP catched already
    fun validateOTP(user: AppUser?, otpCode: Int) : Boolean{

//        val portal = appUserRepository.findByUsername(username)
        user?.let {
            val listOTP = otpRepository.findByAppUser_UserIdAndOtpStatusAndOtpExpiredTimeGreaterThanEqual(user.userId!!, OTPStatus.CREATED)

            var otp = listOTP.find { otp ->  encryptor.checkPassword("$otpCode", otp.otpCode)}
            otp?.let {
                otp.otpStatus = OTPStatus.USED
                otpRepository.save(otp)
                otpCache.invalidate(user.username)
                return true
            }
            return false
            //throw ExpiredException("OTP.Not.Found", listOf(otpCode))
        }
        throw NotFoundException("User.Not.Found", listOf(user!!.username!!.split("#")))
    }

    fun clearOTP(username: String){
        otpCache.invalidate(username)
    }
//    companion object {
//
//        //cache based on username and OPT MAX 8
//        private val EXPIRE_MINS = 5
//    }
}
