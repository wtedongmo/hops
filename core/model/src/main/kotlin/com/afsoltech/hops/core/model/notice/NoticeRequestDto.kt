package com.afsoltech.hops.core.model.notice


data class NoticeRequestDto (

        val noticeNumber: String? = null,
        var notificationDate: String? = null,
        val taxpayerNumber: String? = null,
        val taxpayerRepresentativeNumber: String? = null,
        var paymentDate: String? = null
)



data class UnpaidNoticeRequestDto (
        val noticeNumber: String? = null,
        var notificationDate: String? = null,
        val taxpayerNumber: String?= null,
        val taxpayerRepresentativeNumber: String? = null,
        var dueDate: String? = null
)

