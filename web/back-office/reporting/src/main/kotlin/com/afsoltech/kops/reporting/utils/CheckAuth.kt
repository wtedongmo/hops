package com.nanobnk.epayment.reporting.utils

import com.nanobnk.epayment.model.attribute.UserPrivilege
import com.nanobnk.epayment.model.attribute.UserType
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
class CheckAuth {

    fun hasNanoAdminAuthorization(): Boolean{

        val auth = SecurityContextHolder.getContext().authentication
        val authorities = auth.authorities
        val admin_auth = authorities.find { it -> it.authority.equals(UserType.NANO.name+"_"+ UserPrivilege.ADMIN.name) }
        admin_auth?.let {
            return true }
        return false
    }

    fun hasNanoAuth(auth: Authentication): Boolean{

//        val auth = SecurityContextHolder.getContext().authentication
        val authorities = auth.authorities
        val admin_auth = authorities.find { it ->
            it.authority.equals(UserType.NANO.name+"_"+ UserPrivilege.ADMIN.name) ||
                    it.authority.equals(UserType.NANO.name+"_"+ UserPrivilege.READ_ONLY.name) }
        admin_auth?.let {
            return true
        }
        return false
    }

    fun hasProviderAuth(auth: Authentication): Boolean{

//        val auth = SecurityContextHolder.getContext().authentication
        val authorities = auth.authorities
        val provider_auth = authorities.find { it ->
            it.authority.equals(UserType.PROVIDER.name+"_"+ UserPrivilege.ADMIN.name) ||
                    it.authority.equals(UserType.PROVIDER.name+"_"+ UserPrivilege.READ_ONLY.name) }
        provider_auth?.let {
            return true
        }
        return false
    }

    fun hasParticipantAuth(auth: Authentication): Boolean{

//        val auth = SecurityContextHolder.getContext().authentication
        val authorities = auth.authorities
        val participant_auth = authorities.find { it ->
            it.authority.equals(UserType.PARTICIPANT.name+"_"+ UserPrivilege.ADMIN.name) ||
                    it.authority.equals(UserType.PARTICIPANT.name+"_"+ UserPrivilege.READ_ONLY.name) }
        participant_auth?.let {
            return true
        }
        return false
    }
}