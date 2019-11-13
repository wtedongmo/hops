package com.nanobnk.epayment.portal.service

import com.nanobnk.epayment.model.inbound.*
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service("auth_service_core_portal")
class PortalCheckUserInfosService(val restTemplate: RestTemplate) {

    companion object : KLogging()

    @Value("\${outbound.epayment.customs.authUrl}")
    lateinit var userAuthURL: String



    fun checkUserInfos(authRequest: AuthRequestDto): AuthResponseDto? {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(authRequest, headers)
        val responce = restTemplate.exchange(userAuthURL, HttpMethod.POST, entity, AuthResponseDto::class.java)
        var authResponse = responce.body

        return authResponse
    }

}