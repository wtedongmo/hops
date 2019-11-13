package com.nanobnk.epayment.reporting.service.global

import com.nanobnk.epayment.reporting.service.AbstractNoticeReportServiceTranslate
import com.nanobnk.epayment.reporting.utils.PaidNoticeAppName
import com.nanobnk.epayment.reporting.utils.PaidNoticeReportModel
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class PaidNoticeListGlobalReportService : AbstractNoticeReportServiceGlobalTranslate() {

    @Value("\${paid-customs-list-report}")
    lateinit var reportDetail: String


    @Throws(Exception::class)
    fun genericPaidNoticeListReport(paidNoticeModel: PaidNoticeReportModel) : Map<String, Any>{

        val listMap = parsePaidNoticeNameValue(paidNoticeModel)
        val mapVal = listMap.get(0)
        val mapCols = listMap.get(1)
        val appReportName = env.getProperty("global-paid-customs-list-report")

        val mapColsSt= mapCols.map { entry ->
            entry.key to entry.value.toString()
        }.toMap()
        return buildReport(appReportName, mapVal, mapColsSt, paidNoticeModel.paidNoticeTimeLate) //
    }


    private fun parsePaidNoticeNameValue(paidNoticeModel: PaidNoticeReportModel): List<Map<String, Any?>>{
        val mapVal = hashMapOf<String, Any?>()
        val mapCols = hashMapOf<String, String>()

        mapVal.put("endDate", paidNoticeModel.endDate)
        mapCols.put("endDate", "payment_date")
        mapVal.put("startDate", paidNoticeModel.startDate)
        mapCols.put("startDate", "payment_date")
//        var office = paidNoticeModel.office
        mapVal.put("office", paidNoticeModel.office) //!!.trimToNull()
        if (!paidNoticeModel.office.isNullOrBlank()) {
            mapCols.put("office", "office_name")
        }

        mapVal.put("notice_type", paidNoticeModel.noticeType)
        if (!paidNoticeModel.noticeType.isNullOrBlank()) {
            mapCols.put("notice_type", "notice_type")
        }

        mapVal.put("participant", paidNoticeModel.participant)
        if (!paidNoticeModel.participant.isNullOrBlank()) {
            mapCols.put("participant", "participant_name")
        }

        mapVal.put("beneficiary", paidNoticeModel.beneficiary)
        if (!paidNoticeModel.beneficiary.isNullOrBlank()) {
            mapCols.put("beneficiary", "beneficiary_name")
        }

        mapVal.put("taxpayer", paidNoticeModel.taxpayerNumber)
        if (!paidNoticeModel.taxpayerNumber.isNullOrBlank()) {
            mapCols.put("taxpayer", "taxpayer_number")
        }

        mapVal.put("payment_category", paidNoticeModel.paymentCategory)
        if (!paidNoticeModel.paymentCategory.isNullOrBlank()) {
            mapCols.put("payment_category", "payment_category")
        }

        mapVal.put("payment_method", paidNoticeModel.paymentMethod)
        if (!paidNoticeModel.paymentMethod.isNullOrBlank()) {
            mapCols.put("payment_method", "payment_method")
        }

        mapVal.put("declaration_type", paidNoticeModel.declarationType)
        if (!paidNoticeModel.declarationType.isNullOrBlank()) {
            mapCols.put("declaration_type", "declaration_type")
        }

        mapVal.put("cda_number", paidNoticeModel.taxpayerRepresentNumber)
        if (!paidNoticeModel.taxpayerRepresentNumber.isNullOrBlank()) {
            mapCols.put("cda_number", "cda_number")
        }

        return listOf(mapVal, mapCols)
    }

}

