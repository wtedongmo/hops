package com.afsoltech.hops.core.model.integration

import java.math.BigDecimal
import java.time.Instant

data class PaymentProcessResponseDto (
        val result: String? = null,
        val message: String? = null,
        val epaymentId: Long? = null,
        val bankPaymentNumber: String? = null,
        var camcisPaymentNumber: String? = null,
        var paymentDate: String? = null,
        val paymentResultCode: String? = null,
        val paymentResultMsg: String? = null,
        var noticesList: List<PaymentResultNoticesListDto>? = null
)


data class PaymentOfNoticeResponsesDto(
        val paymentId: Long? = null,
        val bankPaymentNumber: String? = null,
        val bankCode: String? = null,
        val taxpayerNumber: String? = null,
        val accountNumber: String? = null,
        val paymentDate: Instant? = null,
        val totalAmount: BigDecimal? = null,
        val resultMsg: String? = null
//        val resultData: List<String>? = null
)

data class PaymentResultNoticesListDto(
        val noticeNumber: String? = null,
        val resultNoticeMsg: String? = null,
        val resultNoticeCode: String? = null
)
