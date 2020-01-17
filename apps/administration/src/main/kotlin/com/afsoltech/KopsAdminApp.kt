package com.afsoltech

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

//@EnableWebSecurity
@SpringBootApplication//(scanBasePackages = ["com.afsoltech"], exclude = [SecurityAutoConfiguration::class])
class KopsAdminApp

fun main(args: Array<String>) {
    println("loading Kops AdminApp")
    SpringApplication.run(KopsAdminApp::class.java, *args)
    println("loaded Kops AdminApp")
}