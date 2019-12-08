package com.afsoltech.kops.service.integration

import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.exception.UnauthorizedException
import com.afsoltech.core.service.utils.CheckParticipantAPIRequest
import com.afsoltech.kops.core.model.AuthRequestDto
import com.afsoltech.kops.core.model.AuthResponseDto
import com.afsoltech.core.service.utils.LoadBaseDataToMap
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
class CheckUserInfoService(val restTemplate: RestTemplate, val checkParticipantAPIRequest: CheckParticipantAPIRequest) {

    companion object : KLogging()

    @Value("\${api.external.customs.epayment.authUrl}")
    private lateinit var userAuthURL: String

    fun checkUserInfo(authRequest: AuthRequestDto, request: HttpServletRequest?): AuthResponseDto {

        checkParticipantAPIRequest.checkAPIRequest(request)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val bankApiKey = LoadBaseDataToMap.settingMap.get("app.bank.epayment.apikey") ?:
            throw UnauthorizedException("Kops.Error.Payment.Parameter.ApiKey.NotFound")
        headers.add("apikey", bankApiKey.value)

        val entity = HttpEntity(authRequest, headers)
        val responce = restTemplate.exchange(userAuthURL, HttpMethod.POST, entity, AuthResponseDto::class.java)
        var authResponse = responce.body ?: throw BadRequestException("Kops.Error.Parameter.Value")

        return authResponse
    }

}