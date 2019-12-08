package com.afsoltech

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

//@EnableWebSecurity
@SpringBootApplication//(scanBasePackages = ["com.nanobnk"], exclude = [SecurityAutoConfiguration::class])
class KopsPortalApp

fun main(args: Array<String>) {
    println("loading Kops PortalApp")
    SpringApplication.run(KopsPortalApp::class.java, *args)
    println("loaded Kops PortalApp")
}