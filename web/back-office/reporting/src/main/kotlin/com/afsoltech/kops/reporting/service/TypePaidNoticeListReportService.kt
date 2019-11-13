package com.nanobnk.epayment.reporting.service

import com.nanobnk.epayment.service.utils.StringDateFormaterUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TypePaidNoticeListReportService : AbstractNoticeReportService() {


    @Value("\${type-paid-customs-list-report}")
    lateinit var reportDetail: String


    @Throws(Exception::class)
    fun typePaidNoticeListReport(startDate: String, endDate: String, office: String?, noticeType: String?): String { // Must return file name


        val filename = folderToSave + "FR_04_typ_ap_list_details_" + StringDateFormaterUtils.DateTimeToString.formatFr(LocalDateTime.now()) + ".pdf"

        val mapVal = hashMapOf<String, Any?>()
        val mapCols = hashMapOf<String, String>()
        mapVal.put("endDate", endDate)
        mapCols.put("endDate", "payment_date")
        mapVal.put("startDate", startDate)
        mapCols.put("startDate", "payment_date")
        mapVal.put("office", office)
        if (!office.isNullOrBlank()) {
            mapCols.put("office", "office_name")
        }
        mapVal.put("notice_type", noticeType)
        if (!noticeType.isNullOrBlank()) {
            mapCols.put("notice_type", "notice_type")
        }

        buildReport(filename, reportDetail, mapVal, mapCols)
        return filename
    }
}

