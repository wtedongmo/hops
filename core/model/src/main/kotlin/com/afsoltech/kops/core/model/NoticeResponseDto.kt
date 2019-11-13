package com.afsoltech.kops.core.model

import java.math.BigDecimal
import java.time.LocalDate

//@JsonIgnoreProperties(ignoreUnknown = true)
data class NoticeResponseDto (

        //val noticeId : Long? = null,
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
        val paymentDate: String? = null,
        val amountReceived: BigDecimal? = null,
        val paymentCategory: String? = null,
        var paymentNumber: String? = null,

        var beneficiaryList: List<NoticeBeneficiaryDto>? = null

)

data class NoticeBeneficiaryDto (

        val beneficiaryName: String? = null,
        val beneficiaryCode: String? = null,
        val bankCode: String? = null,
        val accountNumber: String? = null,
        val amount: BigDecimal? =null
)