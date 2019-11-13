package com.nanobnk.epayment.reporting.service

import com.nanobnk.epayment.service.utils.StringDateFormaterUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class DelayPaidNoticeListReportService : AbstractNoticeReportService() {


    @Value("\${delay-paid-customs-list-report}")
    lateinit var reportDetail: String


    @Throws(Exception::class)
    fun delayPaidNoticeListReport(startDate: String, endDate: String, office: String?): String { // Must return file name


        val filename = folderToSave + "FR_03_ap_list_delay_details_" + StringDateFormaterUtils.DateTimeToString.formatFr(LocalDateTime.now()) + ".pdf"

        val mapVal = hashMapOf<String, Any?>()
        val mapCols = hashMapOf<String, String>()
        mapVal.put("endDate", endDate)
        mapCols.put("endDate", "payment_date")
        mapVal.put("startDate", startDate)
        mapCols.put("startDate", "payment_date")
        mapVal.put("office", office)
        if (office != null) {
            mapCols.put("office", "office_name")
        }

        buildReport(filename, reportDetail, mapVal, mapCols)
        return filename
    }
}

