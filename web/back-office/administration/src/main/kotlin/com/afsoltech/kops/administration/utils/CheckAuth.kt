package com.nanobnk.epayment.administration.utils

import com.nanobnk.epayment.model.attribute.UserPrivilege
import com.nanobnk.epayment.model.attribute.UserType
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class CheckAuth {

    fun hasAuthorization(): Boolean{

        val auth = SecurityContextHolder.getContext().authentication
        val authorities = auth.authorities
        val admin_auth = authorities.find { it -> it.authority.equals(UserType.NANO.name+"_"+ UserPrivilege.ADMIN.name) }
        admin_auth?.let {
            return true }
        return false
    }
}