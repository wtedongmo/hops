package com.nanobnk.epayment.portal.service

import com.nanobnk.epayment.model.attribute.UserPrivilege
import com.nanobnk.epayment.portal.repository.AppUserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

import java.util.Arrays

@Service
class PortailUserDetailsService : UserDetailsService {

    @Autowired
    private val userRepository: AppUserRepository? = null

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {

        val user = userRepository!!.findByUsername(username)

        val authority = SimpleGrantedAuthority(UserPrivilege.PRE_AUTH.name) //portal.getRole()

        return User(user!!.username!!,
                user.password!!, Arrays.asList<GrantedAuthority>(authority))
    }

}
