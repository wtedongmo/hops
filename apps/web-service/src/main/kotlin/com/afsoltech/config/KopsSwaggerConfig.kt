package com.afsoltech.config

import com.afsoltech.util.rest.config.AbstractRestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class KopsSwaggerConfig : AbstractRestConfig() {

        @Value("\${api.epayment.core.version}")
        var projectVersion : String = ""

        override fun endpointBasePackage(): String {
            return "com.afsoltech.epayment.core.web"
        }

        override fun restProjectBaseUrl(): String {
            return "/"
        }

        override fun restProjectDescription(): String {
            return "epayment-core-web"
        }

        override fun restProjectName(): String {
            return "epayment-core-web"
        }

        override fun restProjectVersion(): String {
            return "v$projectVersion"
        }


}