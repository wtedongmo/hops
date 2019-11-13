//package com.nanobnk.epayment.core.web
//
//import com.nanobnk.EPaymentRestApp
//import com.nanobnk.epayment.model.inbound.AuthRequestDto
//import com.nanobnk.epayment.model.inbound.NoticeRequestDto
//import com.nanobnk.epayment.model.inbound.UnpaidNoticeRequestDto
//import com.nanobnk.epayment.service.PortalCheckUserInfosService
//import com.nanobnk.epayment.service.PortalListPaidNoticeService
//import com.nanobnk.epayment.service.PortalListUnpaidNoticeService
//import mu.KLogging
//import org.junit.Assert
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.context.ContextConfiguration
//import org.springframework.test.context.junit4.SpringRunner
//import java.time.LocalDate
//
//
//@RunWith(SpringRunner::class)
//@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)3
//@ContextConfiguration(classes = arrayOf(EPaymentRestApp::class))
//class TestEPaymentPortalService {
//companion object : KLogging()
//
//    @Autowired
//    lateinit var listUnpaidNoticeService: PortalListUnpaidNoticeService
//
//    @Autowired
//    lateinit var listPaidNoticeService: PortalListPaidNoticeService
//
//    @Autowired
//    lateinit var userAuthService: PortalCheckUserInfosService
//
//
//    @Test
//    fun testPortalListPaidNoticeService() {
//        val payNReq = NoticeRequestDto(null, LocalDate.parse("2019-01-10"), "M051200041474C", null, null)
//        val result = listPaidNoticeService.listPaidNotice(payNReq)
//
//        logger.info { result }
//        org.springframework.util.Assert.isTrue(result!!.size>0, "good result service")
//    }
//
//    @Test
//    fun testPortalListUnpaidNoticeService() {
//        val unpaidNReq = UnpaidNoticeRequestDto(null, LocalDate.parse("2019-01-10"), "M051200041474C", null, null)
//        val result = listUnpaidNoticeService.listUnpaidNotice(unpaidNReq)
//
//        logger.info { result }
//        org.springframework.util.Assert.isTrue(result!!.size>0, "good result service")
//    }
//
//
//    @Test
//    fun testUserAuthService() {
//        val unpaidNReq = AuthRequestDto("M000000000042H", "H00002@camcis.com", "R")
//        val result = userAuthService.checkUserInfos(unpaidNReq)
//
//        logger.info { result }
//        Assert.assertEquals("S", result?.resultCode)
////        Assert.assertEquals("Échec", result?.resultMsg)
//        //assertEquals(result.size, "Hello string!")
//    }
//
//
//
//
//}