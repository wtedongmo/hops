package com.nanobnk.epayment.core.backoffice.util

import com.nanobnk.epayment.entity.UserEntity
import com.nanobnk.epayment.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtil(val userRepository: UserRepository) {

    fun getLoggedInUser(): UserEntity {
        val loggedInUsername = SecurityContextHolder.getContext().authentication.principal.toString()
        val loggedInUser = userRepository.findByUsername(loggedInUsername)

        return checkNotNull(value = loggedInUser)
    }
}