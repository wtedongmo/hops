package com.afsoltech.kops.service.ws

import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.service.utils.LoadSettingDataToMap
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.afsoltech.kops.core.model.account.AuthJWTRequestDto
import com.afsoltech.kops.core.model.account.AuthJWTResponseDto
import com.afsoltech.kops.core.model.account.AuthRespDecodedJWTDto
import mu.KLogging
import org.apache.commons.codec.binary.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest

@Service
class AuthJWTService(val env: Environment, val restTemplate: RestTemplate) {

    companion object : KLogging(){
        var bank_apikey :String=""
        var sessionAuthCache: LoadingCache<String, Any?>? = null
    }

    @Value("\${api.external.bank.authJWTUrl}")
    lateinit var userAuthURL: String

//    @Value("\${app.bank.default.device.type}")
    private var defaultDeviceType: String=""

//    @Value("\${app.session.jwt.duration.expiry.second:180}")
//    private var expiryTimeSeconds: Long=180

    init{
        sessionAuthCache = CacheBuilder.newBuilder().expireAfterWrite(LoadSettingDataToMap.expiryTimeSeconds, TimeUnit.SECONDS).build(object :
                CacheLoader<String, Any?>() {
            override fun load(key: String): Any? {
                return Any()
            }
        })

        bank_apikey = LoadSettingDataToMap.settingMap.get("app.bank.core.apikey")?.value?: ""
//        nanobnk_apikey = env.getProperty("app.ussd.bank.nano.apikey")?:""
        sessionAuthCache!!.put("x-afst-apikey", bank_apikey)
    }

    /**
     * Default authentication of user with his phone number and pin
     */
    fun authJWTService(msisdn: String, pin: String, request: HttpServletRequest): AuthJWTResponseDto{
        defaultDeviceType = LoadSettingDataToMap.settingMap.get("app.bank.default.device.type")?.value?: ""
        val authJWTRequestDto = AuthJWTRequestDto( "MOBILE", defaultDeviceType /*"deviceuid-1234"*/, "MOBILE", msisdn,
                "PIN", pin)
        val authJWTResponseDto = authJWTService(authJWTRequestDto, msisdn, request)
        val authDecode = decodeAuthJwt(authJWTResponseDto.jwtKey!!)
        return authJWTResponseDto
    }

    /**
     * Authentication of the user
     */
    fun authJWTService(authRequest: AuthJWTRequestDto, msisdn: String, request: HttpServletRequest): AuthJWTResponseDto {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
//        headers.add("x-afst-auth", request.getHeader("x-nanobnk-auth"))
//        headers.add("x-afst-apikey", nanobnk_apikey)
        val entity = HttpEntity(authRequest, headers)
        val responce = restTemplate.exchange(userAuthURL, HttpMethod.POST, entity, AuthJWTResponseDto::class.java)
        var authResponse = responce.body

        authResponse?.jwtKey?.let {
            sessionAuthCache!!.put(msisdn, authResponse.jwtKey.toString())
            return authResponse
        }

        throw BadRequestException("Error.Jwt.Auth.Null")
    }

    /**
     * Decode user info from JWT auth key
     */
    fun decodeAuthJwt(jwtToken : String): AuthRespDecodedJWTDto? {

        val split_string = jwtToken.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val base64EncodedHeader = split_string[0]
        val base64EncodedBody = split_string[1]
        val base64EncodedSignature = split_string[2]

        logger.trace { "~~~~~~~~~ JWT Header ~~~~~~~" }
        val base64Url = Base64(true)
        val header = String(base64Url.decode(base64EncodedHeader))
        logger.trace {"JWT Header : $header"}


        logger.trace {"~~~~~~~~~ JWT Body ~~~~~~~"}
        val body = String(base64Url.decode(base64EncodedBody))
        logger.trace {"JWT Body : $body"}

        logger.trace {"~~~~~~~~~ JWT JSON ~~~~~~~"}

        val mapper = ObjectMapper()
        val authJwtResp = mapper.readValue(body, AuthRespDecodedJWTDto::class.java)

        sessionAuthCache!!.put(authJwtResp.identifier+"_ID", authJwtResp.links!!.get("BANK_CUSTOMER_ID")?:"")
        sessionAuthCache!!.put(authJwtResp.identifier+"_SPECIFIC_ID", authJwtResp.links!!.get("DEPLOYMENT_SPECIFIC_CUSTOMER_ID")?:"")
        sessionAuthCache!!.put(authJwtResp.identifier+"_ACCOUNT_LIST", authJwtResp.accountNos?: emptyArray<String>())
        sessionAuthCache!!.put(authJwtResp.identifier+"_EMAIL", authJwtResp.customerEmail?:"")
        sessionAuthCache!!.put(authJwtResp.identifier+"_NAME", authJwtResp.customerName?:"")

        return authJwtResp
    }
}