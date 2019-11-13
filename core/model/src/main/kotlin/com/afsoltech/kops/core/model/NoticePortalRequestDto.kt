package com.afsoltech.kops.core.model

import org.jetbrains.annotations.NotNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class NoticePortalRequestDto (

        var noticeNumber: String? = "",
        var notificationDate: String? = null,
        var taxpayerNumber: String?=null,
        var taxpayerRepresentativeNumber: String? = "",
        var paymentDate: String? = null
)



data class UnpaidNoticePortalRequestDto (
        var noticeNumber: String? = null,
        var notificationDate: String? = null,
        var taxpayerNumber: String?=null,
        var taxpayerRepresentativeNumber: String? = null,
        var dueDate: String? = null
)