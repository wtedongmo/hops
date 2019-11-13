package com.afsoltech.kops.core.model.integration

import com.afsoltech.kops.core.model.NoticeBeneficiaryDto
import java.math.BigDecimal

//@JsonIgnoreProperties(ignoreUnknown = true)
data class UnpaidNoticeResponseDto (

        var noticeId : Long? = null,
        val noticeNumber: String? = null,
        val notificationDate: String? = null,
        val noticeType: String? = null,
        val referenceNumber: String? = null,
        val declarationType: String? = null,
        val taxPayerNumber: String? = null,
        val taxPayerName: String? = null,
        val taxPayerRepresentativeNumber: String? = null,
        val taxPayerRepresentativeCode: String? = null,
        val taxPayerRepresentativeName: String? = null,
        val issuerOffice: String? = null,
        val dueDate: String? = null,
        val noticeAmount: BigDecimal? = null,

        var beneficiaryList: List<NoticeBeneficiaryDto>? = null

)
