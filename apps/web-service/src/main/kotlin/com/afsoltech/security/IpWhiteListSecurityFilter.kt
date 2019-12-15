package com.afsoltech.security

import com.afsoltech.core.exception.ForbiddenException
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.env.Environment
import org.springframework.stereotype.Service
import org.springframework.web.filter.GenericFilterBean
import javax.annotation.PostConstruct
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Service
class IpWhiteListSecurityFilter (val env: Environment) : GenericFilterBean() {
    companion object : KLogging()

//    @Value("#{T(java.util.Arrays).asList('\${api.security.ip.whitelist:}')}")

//    @Value("\${api.security.ip.whitelist}")
//    lateinit var ipWhiteListString: String

//    @Value("\${api.security.ip.whitelist}")
//    lateinit var ipWhite: String //= emptyList<String>()
    var ipWhitelist=emptyList<String>()

//    @PostConstruct
//    fun init() {
////        if(!ipWhite.isNullOrBlank()){
////            ipWhitelist = ipWhite.split(",")
////        }
//        logger.trace { "IP whitelist: $ipWhitelist" }
//    }

    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {

        val request = req as HttpServletRequest
        val response = res as HttpServletResponse


        if(ipWhitelist.isEmpty()) {
            val ipWhiteListString = env.getProperty("api.security.ip.whitelist")
            if (!ipWhiteListString.isNullOrBlank())
                ipWhitelist = ipWhiteListString.split(",")
        }

        val key = req.getHeader("apikey")
        if (ipWhitelist.isNotEmpty()) {
            val ip = request.remoteAddr
            logger.trace { "Checking $ip in whitelist" }

            if (!ipWhitelist.contains(ip))
                throw ForbiddenException("IP.Restricted", listOf(ip))
        }

        chain.doFilter(request, response)

    }

}
