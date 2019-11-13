package com.afsoltech.kops.core.model

import java.math.BigDecimal
import java.time.LocalDate

//@JsonIgnoreProperties(ignoreUnknown = true)
data class NoticePortalResponseDto (

        val noticeNumber: String? = null,
        var notificationDate: String?=null,
        var noticeType: String? = null,
        val referenceNumber: String? = null,
        var declarationType: String? = null,
        val taxPayerNumber: String? = null,
        val taxPayerName: String? = null,
        val taxPayerRepresentativeNumber: String? = null,
        val taxPayerRepresentativeCode: String? = null,
        val taxPayerRepresentativeName: String? = null,
        var issuerOffice: String? = null,
        var paymentNumber: String? = null,
        var paymentDate: String? = null,
        var amountReceived: String? = null,
        var paymentCategory: String? = null

        )
