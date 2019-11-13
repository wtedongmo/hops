package com.nanobnk.config

import com.nanobnk.security.IpWhiteListSecurityFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class SecurityConfig {

    @Autowired
    lateinit var env: Environment
//    @Autowired
//    lateinit var inboundParticipantRepository: InboundParticipantRepository

//    @Bean
//    fun registerApiKeySecurityFilter(): FilterRegistrationBean {
//        val filter= ApiKeySecurityFilter(inboundParticipantRepository)
//        val reg = FilterRegistrationBean(filter)
//        reg.order = 4
//        return reg
//    }

    @Bean
    fun registerIpSecurityFilter(): FilterRegistrationBean {
        val filter= IpWhiteListSecurityFilter(env)
        val reg = FilterRegistrationBean(filter)
//        reg.order = 5
        return reg
    }

}