package com.afsoltech.hops.core.model.notice


data class NoticeResponses<T>(
        val resultCode: String? = null,
        val resultMsg: String? = null,
        var resultData: List<T>? = null
) {
    fun result() = resultData ?: emptyList()
}