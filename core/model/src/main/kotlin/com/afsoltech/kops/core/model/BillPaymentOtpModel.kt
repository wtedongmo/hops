package com.afsoltech.kops.core.model

import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.core.model.notice.BillFeeDto

data class BillPaymentOtpModel(
        val otp: String? = null,
        val accountNumber: String? = null,
        val selectedNotices: List<UnpaidNoticeResponseDto>? = null,
        val billFee: BillFeeDto? = null
)