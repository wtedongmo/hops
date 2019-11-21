package com.afsoltech.kops.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import org.springframework.transaction.annotation.EnableTransactionManagement


@EnableTransactionManagement
@Configuration
class KopsConfig {


    @Bean
    fun getRestTemplate(): RestTemplate {
        val jackson = MappingJackson2HttpMessageConverter()
        jackson.supportedMediaTypes = listOf(MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8, MediaType.TEXT_HTML, MediaType.TEXT_PLAIN)

        return RestTemplateBuilder()
                .additionalMessageConverters(jackson)
                .build()
    }

}