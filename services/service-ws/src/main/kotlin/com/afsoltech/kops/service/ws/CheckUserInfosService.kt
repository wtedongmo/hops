package com.afsoltech.kops.service.ws

import com.afsoltech.core.service.utils.CheckParticipantAPIRequest
import mu.KLogging
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class CheckUserInfosService(val restTemplate: RestTemplate , val checkParticipantAPIRequest: CheckParticipantAPIRequest) {

    companion object : KLogging()

//    @Value("\${outbound.ussd.customs.authUrl}")
//    lateinit var userAuthURL: String
//
//    fun checkUserInfos(authRequest: AuthRequestDto): AuthResponseDto {
//        return checkUserInfos(authRequest, null)
//    }
//
//    fun checkUserInfos(authRequest: AuthRequestDto, request: HttpServletRequest?): AuthResponseDto {
//
//        checkParticipantAPIRequest.checkAPIRequest(request)
//
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_JSON
//        val entity = HttpEntity(authRequest, headers)
//        val responce = restTemplate.exchange(userAuthURL, HttpMethod.POST, entity, AuthResponseDto::class.java)
//        var authResponse = responce.body
//
//        return authResponse
//    }

}