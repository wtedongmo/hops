package com.afsoltech

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

//@EnableWebSecurity
@SpringBootApplication//(scanBasePackages = ["com.afsoltech"], exclude = [SecurityAutoConfiguration::class])
class HopsAdminApp

fun main(args: Array<String>) {
    println("loading Hops AdminApp")
    SpringApplication.run(HopsAdminApp::class.java, *args)
    println("loaded Hops AdminApp")
}