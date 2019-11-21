package com.afsoltech.kops.core.model

import org.jetbrains.annotations.NotNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class NoticeRequestDto (

        val noticeNumber: String? = "",
        val notificationDate: String? = null,
        val taxpayerNumber: String? = null,
        val taxpayerRepresentativeNumber: String? = "",
        val paymentDate: String? = null
)



data class UnpaidNoticeRequestDto (
        val noticeNumber: String? = null,
        val notificationDate: String? = null,
        val taxpayerNumber: String?= null,
        val taxpayerRepresentativeNumber: String? = null,
        val dueDate: String? = null
)

