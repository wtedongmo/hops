//package com.afsoltech.epayment.core.web
//
//import com.afsoltech.EPaymentRestApp
//import com.afsoltech.epayment.model.inbound.*
//import com.afsoltech.epayment.repository.OutboundNoticeRepository
//import com.afsoltech.epayment.service.CheckUserInfosService
//import com.afsoltech.epayment.service.ListPaidNoticeService
//import com.afsoltech.epayment.service.ListUnpaidNoticeService
//import com.afsoltech.epayment.service.PaymentProcessOfNoticesService
//import com.afsoltech.epayment.service.utils.StringDateFormaterUtils
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
//@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(classes = arrayOf(EPaymentRestApp::class))
//class TestEPaymentService {
//companion object : KLogging()
//
//    @Autowired
//    lateinit var listUnpaidNoticeService: ListUnpaidNoticeService
//
//    @Autowired
//    lateinit var listPaidNoticeService: ListPaidNoticeService
//
//    @Autowired
//    lateinit var outboundNoticeRepository: OutboundNoticeRepository
//
//    @Autowired
//    lateinit var paymentProcessService: PaymentProcessOfNoticesService
//
//    @Autowired
//    lateinit var checkUserInfosService: CheckUserInfosService
//
//
//    @Test
//    fun testlistPaidNoticeService() {
//        val payNReq = NoticeRequestDto(null, "20190110", "M051200041474C", null, null)
//        val result = listPaidNoticeService.listPaidNotice(payNReq)
//
//        logger.info { result }
//        org.springframework.util.Assert.isTrue(result.resultCode.equals("S"), "good result service")
//    }
//
//    @Test
//    fun testlistUnpaidNoticeService() {
//        val unpaidNReq = UnpaidNoticeRequestDto(null, "20181207", "M051200041474C", null, null)
////        val unpaidNReq = UnpaidNoticeRequestDto(null, "20190110", "M051200041474C", null, null)
//        val result = listUnpaidNoticeService.listUnpaidNotice(unpaidNReq)
//
//        logger.info { result }
//        org.springframework.util.Assert.isTrue(result!!.resultCode.equals("S"), "good result service")
//    }
//
//
//    @Test
//    fun testPaymentOfNoticeService() {
//        val unpaidNReq = UnpaidNoticeRequestDto(null, "", "M051200041474C", null, null)
//        val resultList = listUnpaidNoticeService.listUnpaidNotice(unpaidNReq)
//        //val noticeEntity = outboundNoticeRepository.findByNoticeNumber("CMDP12019GEN000011I") //?: NoticeEntity(1)
//        val notice = resultList!!.result().first()
//        val listNotice = listOf(NoticePaymentSummaryDto( notice!!.noticeId!!, notice!!.noticeNumber!!,
//                notice!!.noticeAmount!!))
//        val payReq = PaymentProcessRequestDto("NNB012019091300012", "10005", null,
//                notice.taxPayerNumber!!, notice!!.noticeAmount!!,
//                "20190913152033", "0244563109647", null, "002", listNotice)
//        val result = paymentProcessService.paymentOfNoticeProcess(payReq)
//        Assert.assertNotNull(result)
//        logger.info { result }
//        Assert.assertEquals("S", result?.result)
////        Assert.assertEquals("Échec", result?.resultMsg)
//        //assertEquals(result.size, "Hello string!")
//    }
//
//
//
//
////    @Test
////    fun paymentProcessOfNoticeServiceTestMany() {
////        val unpaidNReq = UnpaidNoticeRequestDto(null, LocalDate.parse("2018-10-23"), "M051200041474C", null, null)
////        val resultList = listUnpaidNoticeService.listUnpaidNotice(unpaidNReq)
////        //val noticeEntity = outboundNoticeRepository.findByNoticeNumber("CMDP12019GEN000011I") //?: NoticeEntity(1)
////        val listNotice = mutableListOf<NoticePaymentSummaryDto>()
////        val noticesNumer = listOf<String>("CMDP12018GEN000569I", "CMDP12018GEN000602I")
////        var totalAmount = 0.0
////        noticesNumer.forEach { numer ->
////            val customs = resultList!!.result().find { it -> it.noticeNumber.equals(numer)}
////            customs?.let {
////                listNotice.add(NoticePaymentSummaryDto(customs.noticeId!!, customs.noticeNumber!!, customs.noticeAmount!!))
////                totalAmount += customs.noticeAmount!!.toDouble()
////            }
////        }
////        val payReq = PaymentProcessRequestDto("NNB012019030700005", "10001", null,
////                "M051200041474C", totalAmount.toBigDecimal(),
////                StringDateFormaterUtils.StringToDateTime.parse("20190311091233"), "012456310248", null, "CARTE", listNotice)
////        val result = paymentProcessService.paymentOfNoticeProcess(payReq)
////        Assert.assertNotNull(result)
////        logger.info { result }
//////        Assert.assertEquals("Succès", result?.resultMsg)
////        Assert.assertEquals("E", result?.result)
////        //assertEquals(result.size, "Hello string!")
////    }
//
//
////    @Test
////    fun testLinkEntity(){
////        val unpaidNReq = UnpaidNoticeRequestDto("CMDP12019GEN000011I", LocalDate.parse("2019-01-10"), "M051200041474C", null, null)
////        val resultList = listUnpaidNoticeService.listUnpaidNotice(unpaidNReq)
////        logger.info { resultList }
////        val noticeEntity = outboundNoticeRepository.findByNoticeNumber("CMDP12019GEN000011I")
////        val listBenef = noticeEntity?.listNoticeBeneficiary
////        logger.info { listBenef }
////    }
//
//    @Test
//    fun testCheckUserInfosService() {
//        val authReq = AuthRequestDto("M000000000003H", "bosco.kuate@campass.org", "R")
//        val result = checkUserInfosService.checkUserInfos(authReq)
//
//        logger.info { result }
//        org.springframework.util.Assert.isTrue(result.resultCode.equals("S"), "good result service")
//    }
//
//}