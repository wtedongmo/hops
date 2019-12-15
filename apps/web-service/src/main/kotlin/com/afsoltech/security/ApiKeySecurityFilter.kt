//package com.afsoltech.security
//
//import com.afsoltech.epayment.repository.InboundParticipantRepository
//import com.afsoltech.util.rest.error.ForbiddenException
//import mu.KLogging
//import org.springframework.beans.factory.annotation.Value
//import org.springframework.stereotype.Service
//import org.springframework.web.filter.GenericFilterBean
//import javax.annotation.PostConstruct
//import javax.servlet.FilterChain
//import javax.servlet.ServletRequest
//import javax.servlet.ServletResponse
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//
//@Service
//class ApiKeySecurityFilter (val inboundParticipantRepository: InboundParticipantRepository) : GenericFilterBean() {
//    companion object : KLogging() {
//        val userHeader = "x-afst-internal-user"
//        val keyHeader = "x-afst-apikey"
//    }
//
////    @Value("")
////    lateinit var apikey: String
//
//    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
//
//        val request = req as HttpServletRequest
//        val response = res as HttpServletResponse
//
//        val key = request.getHeader(keyHeader)
//        val inboundParticipant = inboundParticipantRepository.findByInboundParticipantApiKeyValue(key)
////        if (apikey != key) throw ForbiddenException("Apikey.Restricted")
//        if (inboundParticipant==null) throw ForbiddenException("Apikey.Restricted")
//
//        chain.doFilter(request, response)
//
//    }
//
//}
