package com.afsoltech.hops.core.model.account

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

data class AuthJWTRequestDto(
        val identifierType:String,
        val deviceType:String,
        val deviceUid:String,
        var identifier:String,
        var loginType:String,
        var pin:String
)

data class AuthJWTResponseDto(
        val jwtKey:String?=null
)

@JsonIgnoreProperties
data class AuthRespDecodedJWTDto(
        val sub:String?=null,
        val iss:String?=null,
        val aud:String?=null,
        val iat:Long?=null,
        val nbf:Long?=null,
        val exp:Long?=null,
        val identifier:String?=null,
        val device:String?=null,
        val lastLogin:String?=null,
        val status:String?=null,
        val scopes:Array<String>?=null,
//        @JsonIgnore
        val links: HashMap<String, String>?=null,
        val system:String?=null,
        val accountNos:Array<String>?=null,
//        @JsonIgnore
        val accountCurrency:Array<HashMap<String, String>>?=null,
        val customerName:String?=null,
        val customerEmail:String?=null,
        val portfolioManager:String?=null
)

data class JWTLinksDto(
        val BANK_CUSTOMER_ID:String?=null,
        val WALLET_CUSTOMER_ID:String?=null,
        val DEPLOYMENT_SPECIFIC_CUSTOMER_ID:String?=null
)
