package com.nanobnk.config

import com.nanobnk.util.rest.config.AbstractRestConfig
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class EPaymentSwaggerConfig : AbstractRestConfig() {

        @Value("\${api.epayment.core.version}")
        var projectVersion : String = ""

        override fun endpointBasePackage(): String {
            return "com.nanobnk.epayment.core.web"
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