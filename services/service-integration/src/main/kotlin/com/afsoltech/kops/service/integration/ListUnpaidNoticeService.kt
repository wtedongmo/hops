package com.afsoltech.kops.service.integration

import com.afsoltech.core.exception.BadRequestException
import com.afsoltech.core.exception.UnauthorizedException
import com.afsoltech.kops.core.model.notice.NoticeResponses
import com.afsoltech.kops.core.model.notice.UnpaidNoticeRequestDto
import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.core.service.utils.LoadSettingDataToMap
import com.afsoltech.core.service.utils.TranslateUtils
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.util.concurrent.TimeUnit
import javax.servlet.http.HttpServletRequest


@Service
class ListUnpaidNoticeService(
        val restTemplate: RestTemplate
) {

    companion object : KLogging(){
        var unpaidNoticeCache: LoadingCache<String, UnpaidNoticeResponseDto>?=null
    }

    @Value("\${api.external.customs.epayment.listUnpaidNoticeUrl}")
    private lateinit var listUnpaidNoticeURL: String

    @Autowired
    private lateinit var translateUtils: TranslateUtils

//    @Value("\${app.notice.expired.duration.unpaid:60}")
    var expiryTimeMinute: Long=60

//    @Value("\${api.customs.epayment.bank.apikey}")
//    lateinit var bankApiKey: String

    init {
        expiryTimeMinute = LoadSettingDataToMap.settingMap.get("app.notice.expired.duration.unpaid")?.value?.toLong()?: 10

        unpaidNoticeCache = CacheBuilder.newBuilder().expireAfterWrite(expiryTimeMinute, TimeUnit.MINUTES).build(object :
                CacheLoader<String, UnpaidNoticeResponseDto>() {
            override fun load(key: String): UnpaidNoticeResponseDto? {
                return UnpaidNoticeResponseDto()
            }
        })
    }

    fun listUnpaidNotice(noticeRequest: UnpaidNoticeRequestDto, request: HttpServletRequest?): NoticeResponses<UnpaidNoticeResponseDto> {

//        checkParticipantAPIRequest.checkAPIRequest(request)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        LoadSettingDataToMap.ePaymentApiKey?.let {
            headers.add("apikey", it)

            val entity = HttpEntity(noticeRequest, headers)
            val responce = restTemplate.exchange(listUnpaidNoticeURL, HttpMethod.POST, entity,
                    object : ParameterizedTypeReference<NoticeResponses<UnpaidNoticeResponseDto>>() {})

            var result = responce.body ?: throw BadRequestException("Error.Parameter.Value")
            logger.trace { "List of UnPaid Notice \n $result" }

            val listUnpaidNotice = result.result()
            listUnpaidNotice.forEach { notice ->
                unpaidNoticeCache!!.put(notice.noticeNumber!!, notice)
            }

            //        if (listUnpaidNotice == null || listUnpaidNotice.isEmpty())
            //            return result
            //        else if(result.resultCode!!.equals("S", true)) {
            //            val outboundNoticeSaveList = updateExistingNotice(listUnpaidNotice)
            //            logger.trace { "Temporary save of Paid Notice" }
            //        }

            return result
        }
        throw UnauthorizedException("Error.Payment.Parameter.ApiKey.NotFound")
    }


//    fun createCsvFile(listUnpaidNotice: List<UnpaidNoticeResponseDto>?) {
//
//        var csvFile = File("csvData.csv")
//
//        if (csvFile.exists()) {
//            csvFile.delete()
//        }
//
//        csvFile.createNewFile()
//
//        val fileWriter = FileWriter(csvFile)
//        val csvWriter = CSVWriter(fileWriter)
////        csvWriter.writeNext(captions.toTypedArray())
////        csvWriter.writeNext(listOf<String>("noticeNumber", "taxpayerNumber").toTypedArray())
//
//        try {
//            listUnpaidNotice?.forEach {it ->
//                val lineData= mutableListOf<String?>()
//                lineData.add(it.noticeNumber)
//                lineData.add(it.taxPayerNumber)
//                csvWriter.writeNext(lineData.toTypedArray())
//            }
//        } catch (ex:Exception) {
//            ex.printStackTrace()
//        } finally {
//            try {
//                csvWriter.flush()
//                csvWriter.close()
//            } catch (ex:Exception) {
//                ex.printStackTrace()
//            }
//        }
//    }
}
