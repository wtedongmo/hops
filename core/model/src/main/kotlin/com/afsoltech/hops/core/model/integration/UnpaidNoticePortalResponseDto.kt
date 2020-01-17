package com.afsoltech.hops.core.model.integration

import java.math.BigDecimal
import java.time.LocalDate

//@JsonIgnoreProperties(ignoreUnknown = true)
data class UnpaidNoticePortalResponseDto (

        val remoteNoticeId: Long? = null,
        val noticeNumber: String? = null,
        var notificationDate: String? = null,
        var noticeType: String? = null,
        val referenceNumber: String? = null,
        var declarationType: String? = null,
        val taxPayerNumber: String? = null,
        val taxPayerName: String? = null,
        val taxPayerRepresentativeNumber: String? = null,
        val taxPayerRepresentativeCode: String? = null,
        val taxPayerRepresentativeName: String? = null,
        var issuerOffice: String? = null,
        var dueDate: String? = null,
        val noticeAmount: String? = null
)
