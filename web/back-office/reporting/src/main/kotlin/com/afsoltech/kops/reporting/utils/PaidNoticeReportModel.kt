package com.nanobnk.epayment.reporting.utils

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.joda.time.LocalDate

@JsonIgnoreProperties(ignoreUnknown = true)
data class PaidNoticeReportModel (
        var startDate: String= LocalDate.now().minusMonths(1).toString(),
        var endDate: String=LocalDate.now().toString(),
        var office: String?=null,
        var noticeType: String?=null,
        var participant: String?=null,
        var beneficiary: String?=null,
        var taxpayerNumber: String?=null,
        var taxpayerRepresentNumber: String?=null,
        var paymentCategory: String?=null,
        var paymentMethod: String?=null,
        var declarationType: String?=null,
        var paidNoticeTimeLate: String?=null,
        var noticeNumber: String?=null,
        var transactionNumber: String?=null,
        var reportCode: Int=101
)