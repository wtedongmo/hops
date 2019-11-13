package com.afsoltech.kops.core.model


data class NoticeResponses<T>(
        val resultCode: String? = null,
        val resultMsg: String? = null,
        var resultData: List<T>? = null
) {
    fun result() = resultData ?: emptyList()
}