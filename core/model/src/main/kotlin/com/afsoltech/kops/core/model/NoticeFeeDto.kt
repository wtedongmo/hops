package com.afsoltech.kops.core.model

import java.math.BigDecimal


data class FeeResponseDto (
        val resultCode: String?, //?=null
        val resultMsg: String?,
        val fee : BillFeeDto?=null
)


data class BillFeeDto (
       val amount: BigDecimal,
        val feeAmount: BigDecimal,
        val totalAmount: BigDecimal
)