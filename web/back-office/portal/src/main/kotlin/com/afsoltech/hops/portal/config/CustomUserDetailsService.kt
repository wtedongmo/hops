package com.afsoltech.core.config

import com.afsoltech.core.entity.user.UserApp
import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.model.user.attribute.UserPrivilege
import com.afsoltech.core.model.user.attribute.UserType
import com.afsoltech.core.repository.user.UserAppRepository
import com.afsoltech.core.service.utils.LoadSettingDataToMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime

import java.util.Arrays

//@Services
class CustomUserDetailsService { //: UserDetailsService

//    @Autowired
//    lateinit var bCryptPasswordEncoder: BCryptPasswordEncoder

    //    @Value("\${app.login.attempt.number:5}")
    var DEFAULT_ATTEMPT_NUMBER = 5
    //    @Value("\${app.login.attempt.time:10}")
    var DEFAULT_ATTEMPT_TIME = 10
    init {
        DEFAULT_ATTEMPT_NUMBER = LoadSettingDataToMap.settingMap.get("app.login.attempt.number")?.value?.toInt()?: 5
        DEFAULT_ATTEMPT_TIME = LoadSettingDataToMap.settingMap.get("app.login.attempt.time")?.value?.toInt()?: 5

    }


    @Autowired
    private val userRepository: UserAppRepository? = null

    @Throws(UsernameNotFoundException::class)
    /*override*/ fun loadUserByUsername(username: String): UserDetails {

        val userOp = userRepository!!.findOneByUsername(username)
        val auth = SecurityContextHolder.getContext().authentication
        val password = auth.credentials.toString()
        val user = userOp.get()

        val isBlocked = verifyUserBlockStatus(user)
        if (!isBlocked) {
            val validPaas = isPasswordVerified(user.password!!, password)
//            if((username.equals("admin") || username.equals("admin")) && password.equals("admin0"))
//            else isPasswordVerified(user.password!!, username+password)
            if (isUserAllowed(user) && validPaas){

                resetLoginAttempts(user)
                val authority = SimpleGrantedAuthority(user.type!!.name+ "_" + UserPrivilege.ADMIN.name) //portal.getRole()
                return User(user.login!!, user.password!!, user.isEnabled!!, isAccountNonExpired(user.expiredDate!!), true, true,
                        getAuthorities(user.type!!, UserPrivilege.ADMIN))
            }
        }

        incrementLoginAttempt(user)
        throw UsernameNotFoundException("User $username was not found or not match")
    }

    private fun verifyUserBlockStatus(user: UserApp): Boolean {

        var isBlocked = false

        if (user.loginAttempts != null) {
            if (user.loginAttempts!! > DEFAULT_ATTEMPT_NUMBER && checkNotNull(user.lastLoginAttempt?.plusSeconds(DEFAULT_ATTEMPT_TIME.toLong())?.
                            isAfter(LocalDateTime.now()))) {
                isBlocked = true
            }
        }

        return isBlocked

    }

    private fun isUserAllowed(user: UserApp): Boolean {
        return (user.status?.name.equals(BaseStatus.ACTIVE.name, true))
    }

    private fun resetLoginAttempts(user: UserApp) {
        saveLoginAttempts(user, 0)
    }

//    @Transactional
    fun saveLoginAttempts(user: UserApp, loginAttempt: Int) {
        user.loginAttempts = loginAttempt
        user.lastLoginAttempt = LocalDateTime.now()

        userRepository!!.save(user)
    }


    private fun incrementLoginAttempt(user: UserApp) {
        var loginAttempts = user.loginAttempts
        if (loginAttempts == null) {
            loginAttempts = 1
        } else {
            loginAttempts += 1
        }

        saveLoginAttempts(user, loginAttempts)
    }

    private fun getAuthorities(type: UserType, privilege: UserPrivilege): MutableCollection<out GrantedAuthority>? {
        return mutableListOf<GrantedAuthority>(SimpleGrantedAuthority("$type" + "_" + "$privilege"))
    }

    private fun isPasswordVerified(dbEncryptedPassword: String, enteredPlainPassword: String): Boolean {
        val bCryptPasswordEncoder= BCryptPasswordEncoder()
        return bCryptPasswordEncoder.matches(enteredPlainPassword, dbEncryptedPassword)
    }

    private fun isAccountNonExpired(expiredDate: LocalDateTime): Boolean {
        return !expiredDate.isBefore(LocalDateTime.now())
    }
}
