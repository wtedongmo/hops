package com.afsoltech.kops.portal

import com.afsoltech.kops.core.model.AuthRequestDto
import com.afsoltech.kops.core.model.NoticeRequestDto
import com.afsoltech.kops.core.model.UnpaidNoticeRequestDto
import com.afsoltech.kops.service.integration.CheckUserInfoService
import com.afsoltech.kops.service.integration.ListPaidNoticeService
import com.afsoltech.kops.service.integration.ListUnpaidNoticeService
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
class TestKopsPortalService {
companion object : KLogging()

    @Autowired
    lateinit var listUnpaidNoticeService: ListUnpaidNoticeService

    @Autowired
    lateinit var listPaidNoticeService: ListPaidNoticeService

    @Autowired
    lateinit var userAuthService: CheckUserInfoService


    @Test
    fun testPortalListPaidNoticeService() {
        val payNReq = NoticeRequestDto(null, "20190110", "M051200041474C", null, null)
        val result = listPaidNoticeService.listPaidNotice(payNReq).resultData

        logger.info { result }
        org.springframework.util.Assert.isTrue(result!!.size>0, "good result service")
    }

    @Test
    fun testPortalListUnpaidNoticeService() {
        val unpaidNReq = UnpaidNoticeRequestDto(null, "20190110", "M051200041474C", null, null)
        val result = listUnpaidNoticeService.listUnpaidNotice(unpaidNReq, null).resultData

        logger.info { result }
        org.springframework.util.Assert.isTrue(result!!.size>0, "good result service")
    }


    @Test
    fun testUserAuthService() {
        val unpaidNReq = AuthRequestDto("M000000000042H", "H00002@camcis.com", "R")
        val result = userAuthService.checkUserInfo(unpaidNReq, null)

        logger.info { result }
        Assert.assertEquals("S", result?.resultCode)
//        Assert.assertEquals("Échec", result?.resultMsg)
        //assertEquals(result.size, "Hello string!")
    }




}