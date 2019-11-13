package com.nanobnk.epayment.service

import com.nanobnk.epayment.entity.InboundParticipantEntity
import com.nanobnk.epayment.model.attribute.ParticipantStatus
import com.nanobnk.epayment.model.inbound.*
import com.nanobnk.epayment.repository.InboundParticipantRepository
import com.nanobnk.epayment.service.utils.CheckParticipantAPIRequest
import com.nanobnk.util.rest.error.BadRequestException
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest

@Service
class CheckUserInfosService(val restTemplate: RestTemplate , val checkParticipantAPIRequest: CheckParticipantAPIRequest) {

    companion object : KLogging()

    @Value("\${outbound.epayment.customs.authUrl}")
    lateinit var userAuthURL: String

    fun checkUserInfos(authRequest: AuthRequestDto): AuthResponseDto {
        return checkUserInfos(authRequest, null)
    }

    fun checkUserInfos(authRequest: AuthRequestDto, request: HttpServletRequest?): AuthResponseDto {

        checkParticipantAPIRequest.checkAPIRequest(request)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val entity = HttpEntity(authRequest, headers)
        val responce = restTemplate.exchange(userAuthURL, HttpMethod.POST, entity, AuthResponseDto::class.java)
        var authResponse = responce.body

        return authResponse
    }

}