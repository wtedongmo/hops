//package com.afsoltech.epayment.core.web
//
//import com.afsoltech.EPaymentRestApp
//import com.afsoltech.epayment.model.inbound.*
//import com.afsoltech.epayment.repository.OutboundNoticeRepository
//import com.afsoltech.epayment.service.utils.StringDateFormatterUtils
//import mu.KLogging
//import org.junit.Assert
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.boot.test.web.client.TestRestTemplate
//import org.springframework.core.ParameterizedTypeReference
//import org.springframework.http.HttpEntity
//import org.springframework.http.HttpHeaders
//import org.springframework.http.HttpMethod
//import org.springframework.http.MediaType
//import org.springframework.test.context.ContextConfiguration
//import org.springframework.test.context.junit4.SpringRunner
//import java.time.LocalDate
//
//
//@RunWith(SpringRunner::class)
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(classes = arrayOf(EPaymentRestApp::class))
//class TestEPaymentIntegrationRestController {
//companion object : KLogging()
//
//    @Autowired
//    lateinit var testRestTemplate: TestRestTemplate
//
//
//    @Test
//    fun testlistPaidNoticeIntegrationController() {
//
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_JSON
//        val noticeRequest = NoticeRequestDto(null, "20190110", "M051200041474C", null, null)
//        val entity = HttpEntity(noticeRequest, headers)
//        val response = testRestTemplate.exchange("/api/v1/epayment/list-paid-customs",
//                HttpMethod.POST, entity, object : ParameterizedTypeReference<NoticeResponses<NoticeResponseDto>>() {})
//
//        val result = response.body
//        logger.info { result }
//
//        org.springframework.util.Assert.isTrue(result.resultCode.equals("S"), "good result")
//    }
//
//      @Test
//    fun testlistUnpaidNoticeController() {
//        val unpaidNReq = UnpaidNoticeRequestDto(null, "20190110", "M051200041474C", null, null)
//
//          val headers = HttpHeaders()
//          headers.contentType = MediaType.APPLICATION_JSON
//          val entity = HttpEntity(unpaidNReq, headers)
//          val response = testRestTemplate.exchange("/api/v1/epayment/list-unpaid-customs",
//                  HttpMethod.POST, entity, object : ParameterizedTypeReference<NoticeResponses<UnpaidNoticeResponseDto>>() {})
//
//          val result = response.body
//          logger.info { result }
//
//        org.springframework.util.Assert.isTrue(result.resultCode.equals("S"), "good result")
//    }
//
//
//    @Test
//    fun testPaymentOfNoticeController() {
//        val unpaidNReq = UnpaidNoticeRequestDto(null, "20190122", "M051200041474C", null, null)
//
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_JSON
//        val entity = HttpEntity(unpaidNReq, headers)
//        val responseUnpaid = testRestTemplate.exchange("/api/v1/epayment/list-unpaid-customs",
//                HttpMethod.POST, entity, object : ParameterizedTypeReference<NoticeResponses<UnpaidNoticeResponseDto>>() {})
//
//        val notice = responseUnpaid.body.result().first()
//        //val noticeEntity = outboundNoticeRepository.findByNoticeNumber("CMDP12019GEN000011I") //?: NoticeEntity(1)
//        val listNotice = listOf(NoticePaymentSummaryDto( notice!!.noticeId!!, notice!!.noticeNumber!!, notice!!.noticeAmount!!))
//        val payReq = PaymentProcessRequestDto("N000120190307000109", "10001", null,
//                notice!!.taxPayerNumber!!, /*BigDecimal("22579139")*/ notice!!.noticeAmount!!,
//                "20190429091233", "012456310248", null, "CARD", listNotice)
//        //StringDateFormatterUtils.StringToDateTime.parse(
//        val entity2 = HttpEntity(payReq, headers)
//        val response = testRestTemplate.postForEntity("/api/v1/epayment/payment-of-customs",
//                entity2, PaymentProcessResponseDto::class.java) //HttpMethod.POST,
//        val result = response.body
//        Assert.assertNotNull(result)
//        logger.info { result }
//        Assert.assertEquals("F", result.result)
//        //assertEquals(result.size, "Hello string!")
//    }
//
//
//    @Test
//    fun testCheckUserInfosController() {
//        val authReq = AuthRequestDto("M000000000003H", "bosco.kuate@campass.org", "R")
//
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_JSON
//        val entity = HttpEntity(authReq, headers)
//        val response = testRestTemplate.postForEntity("/api/v1/epayment/confirm-user-infos",
//                entity, AuthResponseDto::class.java)
//
//        val result = response.body
//        logger.info { result }
//
//        org.springframework.util.Assert.isTrue(result.resultCode.equals("S"), "good result")
//
//    }
//
//}