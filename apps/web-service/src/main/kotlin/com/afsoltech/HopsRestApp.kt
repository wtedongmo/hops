package com.afsoltech

import mu.KLogging
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(scanBasePackages = arrayOf("com.afsoltech"))
class HopsRestApp{
    companion object : KLogging()
}

fun main(args: Array<String>) {
    println("loading hops web Rest-core")
    SpringApplication.run(HopsRestApp::class.java, *args)

//    val ctx = SpringApplicationBuilder(HopsRestApp::class.java).run()
//    val deleteServ = ctx.getBean(DeleteTemporaryNoticeService::class.java)
//    deleteServ.deleteTemporaryUnpaidNotice()
//    deleteServ.resetTemporaryUnpaidNoticeTable()

    println("loaded  hops web Rest-core")
}