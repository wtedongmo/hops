package com.nanobnk

import com.nanobnk.epayment.service.DeleteTemporaryNoticeService
import mu.KLogging
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication(scanBasePackages = arrayOf("com.nanobnk"))
class EPaymentRestApp{
    companion object : KLogging()
}

fun main(args: Array<String>) {
    println("loading epayment web Rest-core")
    SpringApplication.run(EPaymentRestApp::class.java, *args)

//    val ctx = SpringApplicationBuilder(EPaymentRestApp::class.java).run()
//    val deleteServ = ctx.getBean(DeleteTemporaryNoticeService::class.java)
//    deleteServ.deleteTemporaryUnpaidNotice()
//    deleteServ.resetTemporaryUnpaidNoticeTable()

    println("loaded  epayment web Rest-core")
}