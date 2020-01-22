package com.afsoltech.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import springfox.documentation.swagger2.annotations.EnableSwagger2

@EnableSwagger2
@Configuration
class HopsSwaggerConfig  {

        @Value("\${api.epayment.core.version}")
        var projectVersion : String = ""

        fun endpointBasePackage(): String {
            return "com.afsoltech.epayment.core.web"
        }

        fun restProjectBaseUrl(): String {
            return "/"
        }

        fun restProjectDescription(): String {
            return "epayment-core-web"
        }

        fun restProjectName(): String {
            return "epayment-core-web"
        }

        fun restProjectVersion(): String {
            return "v$projectVersion"
        }


}