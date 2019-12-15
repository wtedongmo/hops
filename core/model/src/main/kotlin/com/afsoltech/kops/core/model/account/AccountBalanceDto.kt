package com.afsoltech.kops.core.model.account

import java.math.BigDecimal

data class AccountBalanceRespDto(
    val resultCode: String, //?=null
    val resultMsg: String,
    val accountNumber: String,
    val balance: BigDecimal?=null
)