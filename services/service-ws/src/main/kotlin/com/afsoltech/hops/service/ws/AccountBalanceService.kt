package com.afsoltech.hops.service.ws

import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.hops.core.model.account.AccountBalanceRespDto
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import javax.servlet.http.HttpServletRequest

@Service
class AccountBalanceService(val restTemplate: RestTemplate) {

    companion object : KLogging()

    @Value("\${api.external.bank.accountBalanceUrl}")
    lateinit var accountBalanceURL: String

    fun getAccountBalance(accountNo: String, request: HttpServletRequest): AccountBalanceRespDto {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
//        headers.add("x-nanobnk-auth", AuthJWTService.sessionAuthCache!!.get(msisdn) as String)
//        headers.add("x-nanobnk-apikey", AuthJWTService.nanobnk_apikey)
        val entity = HttpEntity(null, headers)
        val uri = restTemplate.uriTemplateHandler.expand(accountBalanceURL, accountNo)
        val responce = restTemplate.exchange(uri, HttpMethod.GET, entity, AccountBalanceRespDto::class.java)
        var accountBalance = responce.body

        if(accountBalance==null){
            throw BadRequestException("Error.Account.Balance.API.Answer.Null")
        }
        logger.trace { "\nAccount Balance response : $accountBalance" }
        return accountBalance
    }

}