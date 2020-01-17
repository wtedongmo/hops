package com.afsoltech.core

import com.afsoltech.hops.core.model.notice.AuthRequestDto
import com.afsoltech.hops.core.model.notice.NoticeRequestDto
import com.afsoltech.hops.core.model.notice.UnpaidNoticeRequestDto
import com.afsoltech.hops.service.integration.CheckUserInfoService
import com.afsoltech.hops.service.integration.ListPaidNoticeService
import com.afsoltech.hops.service.integration.ListUnpaidNoticeService
import mu.KLogging
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner


@RunWith(SpringRunner::class)
@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestHopsPortalService {
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