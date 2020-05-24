//package com.afsoltech.epayment.integration.outbound.rest
//
//import com.afsoltech.epayment.core.web.ListPaidNoticeController
//import com.afsoltech.epayment.core.web.ListUnpaidNoticeController
//import com.afsoltech.epayment.core.web.PaymentOfNoticeController
//import com.afsoltech.epayment.integration.outbound.service.test.TestEPaymentService
//import com.afsoltech.epayment.model.inbound.NoticePaymentSummaryDto
//import com.afsoltech.epayment.model.inbound.NoticeRequestDto
//import com.afsoltech.epayment.model.inbound.PaymentProcessRequestDto
//import com.afsoltech.epayment.model.inbound.UnpaidNoticeRequestDto
//import com.afsoltech.epayment.model.outbound.OutboundNoticeRequestDto
//import com.afsoltech.epayment.portal.rest.PortalListPaidNoticeController
//import com.afsoltech.epayment.portal.rest.PortalListUnpaidNoticeController
//import com.afsoltech.epayment.portal.service.PortalCheckUserInfosService
//import com.afsoltech.epayment.repository.OutboundNoticeRepository
//import com.afsoltech.epayment.service.utils.StringDateFormatterUtils
//import mu.KLogging
//import org.junit.Assert
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.test.context.junit4.SpringRunner
//import java.math.BigDecimal
//import java.time.LocalDate
//
//
//@RunWith(SpringRunner::class)
//@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//class TestEPaymentPortalRestController {
//companion object : KLogging()
//
//
//    @Autowired
//    lateinit var listUnpaidNoticeController: PortalListUnpaidNoticeController
//
//    @Autowired
//    lateinit var listPaidNoticeController: PortalListPaidNoticeController
//
//    @Autowired
//    lateinit var userAuthController: PortalCheckUserController
//
//    @Test
//    fun testlistPaidNoticeController() {
//        val payNReq = NoticeRequestDto(null, LocalDate.parse("2019-01-10"), "M051200041474C", null, null)
//        val result = listPaidNoticeController.getListPaidNotice(payNReq)
//
//        logger.info { result }
//
//        org.springframework.util.Assert.isTrue(result.size>0, "good result")
//    }
//
//    @Test
//    fun testlistUnpaidNoticeController() {
//        val unpaidNReq = UnpaidNoticeRequestDto(null, LocalDate.parse("2019-01-10"), "M051200041474C", null, null)
//        val result = listUnpaidNoticeController.getListUnpaidNotice(unpaidNReq)
//
//        logger.info { result }
//
//        org.springframework.util.Assert.isTrue(result.size>0, "good result")
//    }
//
//
//    @Test
//    fun testPaymentOfNoticeService() {
//        val unpaidNReq = UnpaidNoticeRequestDto(null, LocalDate.parse("2019-01-22"), "M051200041474C", null, null)
//        val resultList = listUnpaidNoticeController.getListUnpaidNotice(unpaidNReq)
//        //val noticeEntity = outboundNoticeRepository.findByNoticeNumber("CMDP12019GEN000011I") //?: NoticeEntity(1)
//        val customs = resultList.find { it -> it.noticeNumber.equals("CMDP12019GEN000048I") }
//        val listNotice = listOf(NoticePaymentSummaryDto( customs!!.noticeId!!, customs!!.noticeNumber!!,
//                customs!!.noticeAmount!!))
//        val payReq = PaymentProcessRequestDto("N000120190307000070", "10001", null,
//                "M051200041474C", customs!!.noticeAmount!!,
//                StringDateFormatterUtils.StringToDateTime.parse("20190311091233"), "012456310248", null, "CARTE", listNotice)
//        val result = paymentOfNoticeController.paymentOfNoticeProcess(payReq)
//        Assert.assertNotNull(result)
//        TestEPaymentService.logger.info { result }
//        Assert.assertEquals("Conforme", result?.resultMsg)
//        //assertEquals(result.size, "Hello string!")
//    }
//
//
//}