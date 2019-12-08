package com.afsoltech.kops.core.model

import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto

data class BillPaymentNoticeModel(
        var accountNumber: String? = null,
        var selectedNotices: List<UnpaidNoticeResponseDto>? = null,
        var billFee: BillFeeDto? = null
)