package com.afsoltech.hops.core.model


data class BillPaymentResumeDto(
        var bankName: String? = null,
        var bankCode: String? = null,
        var bankAgency: String? = null,
        var accountNumber: String? = null,
        val accountName: String? = null,
        val transactionNumber: String? = null,
        var camcisPaymentNumber: String?=null,
        var paymentDate: String?=null
)