package com.afsoltech.kops.core.model.integration

import java.math.BigDecimal
import java.time.LocalDateTime

data class PaymentProcessResponseDto (
        val resultCode: String? = null,
        val resultMsg: String? = null,
        val epaymentId: Long? = null,
        val bankPaymentNumber: String? = null,
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
        val paymentDate: LocalDateTime? = null,
        val totalAmount: BigDecimal? = null,
        val resultMsg: String? = null
//        val resultData: List<String>? = null
)

data class PaymentResultNoticesListDto(
        val noticeNumber: String? = null,
        val resultNoticeMsg: String? = null,
        val resultNoticeCode: String? = null
)
