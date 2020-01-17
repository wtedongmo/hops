package com.afsoltech.hops.core.model

import java.math.BigDecimal


data class InitPaymentRequestDto(

        val acntNo: String,
        val amount: BigDecimal,
        val fee: BigDecimal,
        val totalAmount: BigDecimal,
        val customerNumber: String,
        val noticeNumberList : List<String>?
)

data class InitPaymentResponseDto(

        val tempPaymentId: Long,
        val acntNo: String,
        val amount: BigDecimal,
        val fee: BigDecimal,
        val totalAmount: BigDecimal,
        val numberOfNotice: Int

        )

data class ValidatePaymentRequestDto(
        val otp: String,
        val tempPaymentId: Long
)



data class UserAskBankAuthPaymentRequestDto(

        val amount: BigDecimal,
        val fee: BigDecimal,
        val totalAmount: BigDecimal,
        val acntNo: String,
        val entName: String
)

data class AskBankAuthPaymentRequestDto(

        val opCode: String,
        val acntNo: String,
        val customerNo: String,
        val trxRefNo: String,
        val trxDt: String,
        val amount: BigDecimal,
        val fee: BigDecimal,
        val totalAmount: BigDecimal,
        val audTrNum: String?=null,
        val currency: String?=null,
        val billNumberList : List<String>,
        val providerCode: String?=null,
        val servName : String?=null
//        val entName: String
)

data class AskBankAuthPaymentResponseDto(

        val resultCode: String,
        val resultMsg: String,
        val resultData: AskBankAuthPaymentRespDataDto?
)

data class AskBankAuthPaymentRespDataDto(

        val opCode: String,
        val acntNo: String,
        val customerNo: String,
        val trxRefNo: String,
        val trxDt: String,
        val amount: BigDecimal,
        val fee: BigDecimal,
        val totalAmount: BigDecimal,
        val audTrNum: String?=null,
        val currency: String?=null,
        val billNumberList : List<String>?=null,
        val providerCode: String?=null,
        val authCd: String,
        val authRsltCd: String,
        val authRsltMsg: String,
        val newBal: BigDecimal?=null

//        val entName: String,
//        val autoRsltCd: String?=null,
//        val autoRsltMsg: String?=null,
//        val autoCd: String?=null,
//        val newBal: String?=null
)