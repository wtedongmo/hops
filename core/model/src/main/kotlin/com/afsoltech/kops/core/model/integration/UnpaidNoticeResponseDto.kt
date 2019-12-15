package com.afsoltech.kops.core.model.integration

import com.afsoltech.kops.core.model.notice.NoticeBeneficiaryDto
import java.math.BigDecimal

//@JsonIgnoreProperties(ignoreUnknown = true)
data class UnpaidNoticeResponseDto (

        var noticeId : Long? = null,
//        var remoteNoticeId : Long? = null,
        val noticeNumber: String? = null,
        var notificationDate: String? = null,
        val noticeType: String? = null,
        val referenceNumber: String? = null,
        val declarationType: String? = null,
        val taxPayerNumber: String? = null,
        val taxPayerName: String? = null,
        val taxPayerRepresentativeNumber: String? = null,
        val taxPayerRepresentativeCode: String? = null,
        val taxPayerRepresentativeName: String? = null,
        val issuerOffice: String? = null,
        var dueDate: String? = null,
        val noticeAmount: BigDecimal? = null,

        var beneficiaryList: List<NoticeBeneficiaryDto>? = null

)
