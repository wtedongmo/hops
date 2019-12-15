package com.afsoltech.kops.core.model.notice


data class NoticeRequestDto (

        val noticeNumber: String? = null,
        val notificationDate: String? = null,
        val taxpayerNumber: String? = null,
        val taxpayerRepresentativeNumber: String? = null,
        val paymentDate: String? = null
)



data class UnpaidNoticeRequestDto (
        val noticeNumber: String? = null,
        val notificationDate: String? = null,
        val taxpayerNumber: String?= null,
        val taxpayerRepresentativeNumber: String? = null,
        val dueDate: String? = null
)

