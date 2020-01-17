package com.afsoltech.hops.core.model.account

import java.math.BigDecimal
import java.time.LocalDate

data class AccountListDto (
        val accountsList: List<AccountDto>?=null
)

data class AccountDto(
        val accountNo:String,
        val accountType:String?=null,
        val availableBalance:BigDecimal?=null,
        val branch: BranchDto?=null,
        val accountName:String?=null,
        val currency:String?=null,
        val openingDate:LocalDate?=null
)

data class BranchDto(
        val addressLine1:String?=null,
        val town:String?=null,
        val branchCode:String?=null,
        val latitude:String?=null,
        val longitude:String?=null,
        val name:String?=null
        )