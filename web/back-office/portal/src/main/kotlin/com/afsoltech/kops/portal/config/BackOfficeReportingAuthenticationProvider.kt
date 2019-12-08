package com.nanobnk.epayment.reporting.config

import com.afsoltech.core.entity.user.UserApp
import com.afsoltech.core.model.attribute.BaseStatus
import com.afsoltech.core.model.user.attribute.UserPrivilege
import com.afsoltech.core.model.user.attribute.UserType
import com.afsoltech.core.repository.user.UserAppRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class BackOfficeReportingAuthenticationProvider(val userRepository: UserAppRepository, val bCryptPasswordEncoder: BCryptPasswordEncoder) : AuthenticationProvider {

    @Value("\${api.afst.login.attempt.number:5}")
    var DEFAULT_ATTEMPT_NUMBER = 5
    @Value("\${api.epayment.login.attempt.time:10}")
    var DEFAULT_ATTEMPT_TIME = 10

    override fun authenticate(auth: Authentication): Authentication? {
        val username = auth.principal.toString()
        val password = auth.credentials.toString()
        val userOp = userRepository.findByUsername(username)

        if(userOp.isPresent){
            val user  =userOp.get()
            val isBlocked = verifyUserBlockStatus(user)

            if (!isBlocked) {
                val validPaas = if((username.equals("admin") || username.equals("admin")) && password.equals("admin0"))
                                                isPasswordVerified(user.password!!, password)
                            else isPasswordVerified(user.password!!, username+password)
                if (isUserAllowed(user) && validPaas){
                    resetLoginAttempts(user)
                    return UsernamePasswordAuthenticationToken(username, password, getAuthorities(checkNotNull(user.type), checkNotNull(user.privilege)))
                }
            }

            incrementLoginAttempt(user)

        }
        return null
//        return AnonymousAuthenticationToken(username, password, mutableListOf<GrantedAuthority>(SimpleGrantedAuthority("ANONYMOUS")))
    }

    private fun verifyUserBlockStatus(user: UserApp): Boolean {

        var isBlocked = false

        if (user.loginAttempts != null) {
            if (user.loginAttempts!! > DEFAULT_ATTEMPT_NUMBER && checkNotNull(user.lastLoginAttempt?.plusMinutes(DEFAULT_ATTEMPT_TIME.toLong())?.
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

    @Transactional
    fun saveLoginAttempts(user: UserApp, loginAttempt: Int) {
        user.loginAttempts = loginAttempt
        user.lastLoginAttempt = LocalDateTime.now()

        userRepository.save(user)
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
        return bCryptPasswordEncoder.matches(enteredPlainPassword, dbEncryptedPassword)
    }

    override fun supports(p0: Class<*>?): Boolean {
        return checkNotNull(p0?.equals(UsernamePasswordAuthenticationToken::class.java))
    }

}