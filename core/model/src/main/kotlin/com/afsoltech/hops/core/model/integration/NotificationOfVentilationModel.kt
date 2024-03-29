package com.afsoltech.hops.core.model.integration

import com.afsoltech.core.model.attribute.VentilationStatus


data class VentilationRequest(
        val bankPaymentNumber : String,
        val bankCode: String?,
        val ventilationStatus: VentilationStatus,
        val ventilationMessage: String?
)

data class VentilationResponse(
        val resultCode: String,
        val resultMessage: String?,
        val bankPaymentNumber : String?
        )

data class NotifOfVentilRequest(
        val bankPaymentNumber : String,
        val bankCode: String?,
        val benefeciaryList : List<BeneficiaryVentilation>? = null
)

data class BeneficiaryVentilation(
        val noticeNumber: String? = null,
        val beneficiaryCode: String? = null,
        val bankCode: String? = null,
        val accountNumber: String? = null,
        val ventilationStatus: VentilationStatus,
        val ventilationMessage: String?
)


data class NotifOfVentilResponse(
        val resultCode: String,
        val resultMessage: String?,
        val bankPaymentNumber : String?,
        val benefeciaryList : List<BeneficiaryVentilationResp>? = null
)

data class BeneficiaryVentilationResp(
        val resultCode: String,
        val resultMessage: String?,
        val noticeNumber: String? = null,
        val beneficiaryCode: String? = null,
        val accountNumber: String? = null
)
