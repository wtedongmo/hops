package com.afsoltech.kops.core.model.notice

import java.math.BigDecimal

//@JsonIgnoreProperties(ignoreUnknown = true)
data class NoticeResponseDto (

        //val noticeId : Long? = null,
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
        var paymentDate: String? = null,
        val amountReceived: BigDecimal? = null,
        val paymentCategory: String? = null,
        var paymentNumber: String? = null,
        var camcisPaymentNumber: String? = null,

        var beneficiaryList: List<NoticeBeneficiaryDto>? = null

)

data class NoticeBeneficiaryDto (

        val beneficiaryName: String? = null,
        val beneficiaryCode: String? = null,
        val bankCode: String? = null,
        val accountNumber: String? = null,
        val amount: BigDecimal? =null
)