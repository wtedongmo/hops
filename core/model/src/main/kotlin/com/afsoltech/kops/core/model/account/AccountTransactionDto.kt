package com.afsoltech.kops.core.model.account

import java.math.BigDecimal
import java.time.LocalDate

data class AccountTransactionResponseDto(
        val noOfPages: Int?=null,
        val accountTransactions: List<DetailAccountTransactionDto>?=null
)

data class DetailAccountTransactionDto(
    val accountNo: String?=null,
    val transactionDate: LocalDate?=null,
    val transactionDescription: String?=null,
    val amount: BigDecimal?=null,
    val type: String?=null
)