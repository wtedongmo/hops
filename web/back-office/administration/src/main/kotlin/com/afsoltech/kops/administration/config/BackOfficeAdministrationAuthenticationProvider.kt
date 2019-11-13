package com.nanobnk.epayment.administration.config

import com.nanobnk.epayment.entity.UserEntity
import com.nanobnk.epayment.model.attribute.BaseStatus
import com.nanobnk.epayment.model.attribute.UserPrivilege
import com.nanobnk.epayment.model.attribute.UserType
import com.nanobnk.epayment.repository.UserRepository
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class BackOfficeAdministrationAuthenticationProvider(val userRepository: UserRepository, val bCryptPasswordEncoder: BCryptPasswordEncoder)
    : AuthenticationProvider {
    companion object : KLogging()

    @Value("\${api.epayment.login.attempt.number:5}")
    var DEFAULT_ATTEMPT_NUMBER = 5
    @Value("\${api.epayment.login.attempt.time:10}")
    var DEFAULT_ATTEMPT_TIME = 10

    override fun authenticate(auth: Authentication): Authentication? {
        val username = auth.principal.toString()
        val password = auth.credentials.toString()
        val user = userRepository.findByUsername(username)

        user?.let {

            val isBlocked = verifyUserBlockStatus(user)

            if (!isBlocked) {
                val validPaas = if((username.equals("nanoadmin") || username.equals("admin")) && password.equals("nanoadmin")) isPasswordVerified(user.password!!, password)
                                    else isPasswordVerified(user.password!!, username+password)
                if (isUserAllowed(user) && validPaas) {
                    resetLoginAttempts(user)
                    return UsernamePasswordAuthenticationToken(username, password, getAuthorities(checkNotNull(user.type), checkNotNull(user.privilege)))
                }
            }

            incrementLoginAttempt(user)

        }
        return null
//        return AnonymousAuthenticationToken(username, password, mutableListOf<GrantedAuthority>(SimpleGrantedAuthority("ANONYMOUS")))
    }

    private fun isUserAllowed(user: UserEntity): Boolean {
        return (user.type == UserType.NANO && user.status?.name.equals(BaseStatus.ACTIVE.name, true))
    }

    private fun verifyUserBlockStatus(user: UserEntity): Boolean {

        var isBlocked = false

        if (user.loginAttempts != null) {
            if (user.loginAttempts!! > DEFAULT_ATTEMPT_NUMBER && checkNotNull(user.lastLoginAttempt?.plusMinutes(DEFAULT_ATTEMPT_TIME.toLong())?.isAfter(LocalDateTime.now()))) {
                isBlocked = true
            }
        }

        return isBlocked

    }

    private fun resetLoginAttempts(user: UserEntity) {
        saveLoginAttempts(user, 0)
    }

    @Transactional
    fun saveLoginAttempts(user: UserEntity, loginAttempt: Int) {
        user.loginAttempts = loginAttempt
        user.lastLoginAttempt = LocalDateTime.now()

        userRepository.save(user)
    }


    private fun incrementLoginAttempt(user: UserEntity) {
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