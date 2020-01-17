package com.afsoltech

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

//@EnableWebSecurity
@SpringBootApplication//(scanBasePackages = ["com.afsoltech"], exclude = [SecurityAutoConfiguration::class])
class HopsPortalApp

fun main(args: Array<String>) {
    println("loading Hops PortalApp")
    SpringApplication.run(HopsPortalApp::class.java, *args)
    println("loaded Hops PortalApp")
}