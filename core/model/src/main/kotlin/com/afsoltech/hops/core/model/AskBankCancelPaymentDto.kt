package com.afsoltech.hops.core.model

import java.math.BigDecimal



data class AskBankCancelPaymentRequestDto(

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
        val providerCode: String,
        val servName : String?=null,
        val authCode: String
)

data class AskBankCancelPaymentResponseDto(

        val resultCode: String,
        val resultMsg: String,
        val resultData: AskBankCancelPaymentRespDataDto?
)

data class AskBankCancelPaymentRespDataDto(

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
        val providerCode: String,
        val authCode: String,
        val cancelRsltCd: String,
        val cancelRsltMsg: String,
        val newBal: BigDecimal?=null

//        val entName: String,
//        val autoRsltCd: String?=null,
//        val autoRsltMsg: String?=null,
//        val autoCd: String?=null,
//        val newBal: String?=null
)