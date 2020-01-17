package com.afsoltech.hops.core.model.integration

import java.math.BigDecimal

data class PaymentProcessRequestDto(
        val bankPaymentNumber: String,
        val bankCode: String,
        val bankName: String?=null,
        val taxpayerNumber: String,
        val totalAmount: BigDecimal,
        val paymentDate: String,
        val accountNumber: String,
        val accountName: String?=null,
        val paymentMethod: String,

        val noticeList: List<NoticeOfPaymentDto> = emptyList()
)

data class NoticeOfPaymentDto(
        val noticeId: Long,
        val noticeNumber: String,
        val noticeAmount: BigDecimal
)


/*{
    fun result() = resultData ?: emptyList()
}*/


