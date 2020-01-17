package com.afsoltech.hops.core.model

import com.afsoltech.hops.core.model.notice.BillFeeDto


data class BillAndFeeResponses<T>(
        val resultCode: String? = null,
        val resultMsg: String? = null,
        val fee: BillFeeDto?=null,
        var resultData: List<T>? = null
) {
    fun result() = resultData ?: emptyList()
}