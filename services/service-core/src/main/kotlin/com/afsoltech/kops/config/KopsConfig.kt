package com.afsoltech.kops.config

import com.afsoltech.core.repository.SessionLogRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestTemplate
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder
import org.springframework.boot.context.properties.ConfigurationProperties
import javax.sql.DataSource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.*


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