package com.afsoltech.kops.core.model

import com.afsoltech.kops.core.model.integration.UnpaidNoticeResponseDto
import com.afsoltech.kops.core.model.notice.BillFeeDto

data class BillPaymentNoticeModel(
        var otp: String? = null,
        var accountNumber: String? = null,
        val taxpayerNumber: String? = null,
        val selectedBills: List<UnpaidNoticeResponseDto>? = null,
        val billFee: BillFeeDto? = null
)