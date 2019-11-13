package com.afsoltech.kops.core.model

import java.math.BigDecimal
import java.time.LocalDate

//@JsonIgnoreProperties(ignoreUnknown = true)
data class NoticeReportResponseDto (

        var num: String? = null,
        var notice_number: String? = null,
        var notification_date: String?=null,
        var notice_type: String? = null,
        var reference_number: String? = null,
        var declaration_type: String? = null,
        var office_name: String? = null,
        var participant_name: String? = null,
        var taxpayer_number: String? = null,
        var taxpayer_name: String? = null,
        var cda_number: String? = null,
        var cda_code: String? = null,
        var cda_name: String? = null,
        var due_date: String? = null,
        var payment_date: String? = null,
        var payment_amount: String? = null,
        var payment_category: String? = null,
        var payment_method: String? = null,
        var organism: String? = null,
        var string_agg: String? = null,
        var beneficiary_name: String? = null,
        var beneficiary_amount: String? = null
        )
