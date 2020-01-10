package com.afsoltech.kops.core.model.account

import java.math.BigDecimal

data class AccountBalanceRespDto(
    val resultCode: String, //?=null
    val resultMsg: String,
    val accountNo: String,
    val currency: String?=null,
    val balance: BigDecimal?=null
)