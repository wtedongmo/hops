package com.afsoltech.kops.portal

import com.afsoltech.kops.core.model.NoticeRequestDto
import com.afsoltech.kops.core.model.NoticeResponseDto
import com.afsoltech.kops.core.model.UnpaidNoticeRequestDto
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import mu.KLogging
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestKopsPortalIntegrationRestController {
companion object : KLogging()

    @Autowired
    lateinit var testRestTemplate: TestRestTemplate

//    @Autowired
//    lateinit var listUnpaidNoticeController: ListUnpaidNoticeController
//
//    @Autowired
//    lateinit var listPaidNoticeController: ListPaidNoticeController
//
//    @Autowired
//    lateinit var paymentOfNoticeController: PaymentOfNoticeController

    @Test
    fun testlistPaidNoticeIntegrationController() {

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val noticeRequest = NoticeRequestDto(null, "20190110", "M051200041474C", null, null)
        val entity = HttpEntity(noticeRequest, headers)
        val response = testRestTemplate.exchange("/portal/list-paid-notice",
                HttpMethod.POST, entity, object : ParameterizedTypeReference<List<NoticeResponseDto>>() {})

        val result = response.body!!
        logger.info { result }

        org.springframework.util.Assert.isTrue(result.size>0, "good result")
    }

      @Test
    fun testlistUnpaidNoticeController() {
        val unpaidNReq = UnpaidNoticeRequestDto(null, "20190110", "M051200041474C", null, null)

          val headers = HttpHeaders()
          headers.contentType = MediaType.APPLICATION_JSON
          val entity = HttpEntity(unpaidNReq, headers)
          val response = testRestTemplate.exchange("/portal/list-unpaid-notice",
                  HttpMethod.POST, entity, object : ParameterizedTypeReference<List<UnpaidNoticeResponseDto>>() {})

          val result = response.body!!
          logger.info { result }

        org.springframework.util.Assert.isTrue(result.size>0, "good result")
    }


}