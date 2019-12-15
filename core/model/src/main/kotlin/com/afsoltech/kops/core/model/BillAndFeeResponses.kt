package com.afsoltech.kops.core.model

import com.afsoltech.kops.core.model.notice.BillFeeDto


data class BillAndFeeResponses<T>(
        val resultCode: String? = null,
        val resultMsg: String? = null,
        val fee: BillFeeDto?=null,
        var resultData: List<T>? = null
) {
    fun result() = resultData ?: emptyList()
}