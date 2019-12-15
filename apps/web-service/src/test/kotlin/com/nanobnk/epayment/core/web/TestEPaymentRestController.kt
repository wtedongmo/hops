package com.afsoltech.epayment.core.web

import com.afsoltech.epayment.model.inbound.*
import com.afsoltech.epayment.repository.OutboundNoticeRepository
import com.afsoltech.epayment.service.utils.StringDateFormaterUtils
import mu.KLogging
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import java.time.LocalDate


@RunWith(SpringRunner::class)
@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestEPaymentRestController {
companion object : KLogging()


    @Autowired
    lateinit var listUnpaidNoticeController: com.afsoltech.epayment.core.web.ListUnpaidNoticeController

    @Autowired
    lateinit var listPaidNoticeController: com.afsoltech.epayment.core.web.ListPaidNoticeController

    @Autowired
    lateinit var outboundNoticeRepository: OutboundNoticeRepository

    @Autowired
    lateinit var paymentOfNoticeController: com.afsoltech.epayment.core.web.PaymentOfNoticeController

    @Autowired
    lateinit var checkUserInfosController: CheckUserInfosController

    @Test
    fun testlistPaidNoticeController() {
        val payNReq = NoticeRequestDto(null, "20190110", "M051200041474C", null, null)
        val result = listPaidNoticeController.getListPaidNotice(payNReq, null)

        logger.info { result }

        org.springframework.util.Assert.isTrue(result.resultCode.equals("S"), "good result")
    }

    @Test
    fun testlistUnpaidNoticeController() {
        val unpaidNReq = UnpaidNoticeRequestDto(null, "20190110", "M051200041474C", null, null)
        val result = listUnpaidNoticeController.getListUnpaidNotice(unpaidNReq, null)

        logger.info { result }

        org.springframework.util.Assert.isTrue(result.resultCode.equals("S"), "good result")
    }


    @Test
    fun testPaymentOfNoticeController() {
        val unpaidNReq = UnpaidNoticeRequestDto(null, "20190122", "M051200041474C", null, null)
        val resultList = listUnpaidNoticeController.getListUnpaidNotice(unpaidNReq, null)
        //val noticeEntity = outboundNoticeRepository.findByNoticeNumber("CMDP12019GEN000011I") //?: NoticeEntity(1)
        val notice = resultList.result().first()
        val listNotice = listOf(NoticePaymentSummaryDto( notice!!.noticeId!!, notice!!.noticeNumber!!,
                notice!!.noticeAmount!!))
        val payReq = PaymentProcessRequestDto("N000120190307000104", "10001", null,
                "M051200041474C", notice!!.noticeAmount!!,
                "20190429091233", "012456310248", null, "CARTE", listNotice)
        val result = paymentOfNoticeController.paymentOfNoticeProcess(payReq)
        Assert.assertNotNull(result)
        logger.info { result }
        Assert.assertEquals("S", result?.result)
        //assertEquals(result.size, "Hello string!")
    }


    @Test
    fun testCheckUserInfosController() {
        val authReq = AuthRequestDto("M000000000003H", "bosco.kuate@campass.org", "R")
        val result = checkUserInfosController.checkUserInfos(authReq, null)

        logger.info { result }
        org.springframework.util.Assert.isTrue(result.resultCode.equals("S"), "good result service")
    }


}