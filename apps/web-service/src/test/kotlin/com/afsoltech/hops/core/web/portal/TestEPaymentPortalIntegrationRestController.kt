//package com.afsoltech.epayment.core.web.portal
//
//import com.afsoltech.EPaymentRestApp
//import com.afsoltech.epayment.model.inbound.NoticeRequestDto
//import com.afsoltech.epayment.model.inbound.NoticeResponseDto
//import com.afsoltech.epayment.model.inbound.UnpaidNoticeRequestDto
//import com.afsoltech.epayment.model.inbound.UnpaidNoticeResponseDto
//import mu.KLogging
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
//class TestEPaymentPortalIntegrationRestController {
//companion object : KLogging()
//
//    @Autowired
//    lateinit var testRestTemplate: TestRestTemplate
//
//
//    @Test
//    fun testPortalListPaidNoticeIntegrationController() {
//
//        val headers = HttpHeaders()
//        headers.contentType = MediaType.APPLICATION_JSON
//        val noticeRequest = NoticeRequestDto(null, "2019-01-10", "M051200041474C", null, null)
//        val entity = HttpEntity(noticeRequest, headers)
//        val response = testRestTemplate.exchange("/v1/epayment/portal/list-paid-customs",
//                HttpMethod.POST, entity, object : ParameterizedTypeReference<List<NoticeResponseDto>>() {})
//
//        val result = response.body
//        logger.info { result }
//
//        org.springframework.util.Assert.isTrue(result.size>0, "good result")
//    }
//
//      @Test
//    fun testPortalListUnpaidNoticeController() {
//        val unpaidNReq = UnpaidNoticeRequestDto(null, "2019-01-10", "M051200041474C", null, null)
//
//          val headers = HttpHeaders()
//          headers.contentType = MediaType.APPLICATION_JSON
//          val entity = HttpEntity(unpaidNReq, headers)
//          val response = testRestTemplate.exchange("/v1/epayment/portal/list-unpaid-customs",
//                  HttpMethod.POST, entity, object : ParameterizedTypeReference<List<UnpaidNoticeResponseDto>>() {})
//
//          val result = response.body
//          logger.info { result }
//
//        org.springframework.util.Assert.isTrue(result.size>0, "good result")
//    }
//
//
//}