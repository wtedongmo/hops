package com.afsoltech

import mu.KLogging
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration

//@EnableWebSecurity
//@EnableSwagger2
@SpringBootApplication(scanBasePackages = ["com.afsoltech"], exclude = [SecurityAutoConfiguration::class])
class HopsAdminApp{
    companion object : KLogging()
}

fun main(args: Array<String>) {
    println("loading Hops AdminApp")
    SpringApplication.run(HopsAdminApp::class.java, *args)
    println("loaded Hops AdminApp")
}