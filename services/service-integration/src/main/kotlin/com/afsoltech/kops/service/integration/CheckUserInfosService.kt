package com.afsoltech.kops.service.integration

import com.afsoltech.core.exception.UnauthorizedException
import com.afsoltech.core.service.utils.CheckParticipantAPIRequest
import com.afsoltech.kops.core.model.AuthRequestDto
import com.afsoltech.kops.core.model.AuthResponseDto
import com.afsoltech.kops.service.utils.LoadBaseDataToMap
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

    fun checkUserInfos(authRequest: AuthRequestDto, request: HttpServletRequest?): AuthResponseDto? {

        checkParticipantAPIRequest.checkAPIRequest(request)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val bankApiKey = LoadBaseDataToMap.parameterDataMap.get("api.epayment.bank.apikey") ?:
            throw UnauthorizedException("Kops.Error.Payment.Parameter.ApiKey.NotFound")
        headers.add("apikey", bankApiKey.value)

        val entity = HttpEntity(authRequest, headers)
        val responce = restTemplate.exchange(userAuthURL, HttpMethod.POST, entity, AuthResponseDto::class.java)
        var authResponse = responce.body

        return authResponse
    }

}