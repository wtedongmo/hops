package com.nanobnk.epayment.model.attribute

enum class RequestTypePortal {
    LOGIN, OTP_VALIDATION, OTP_RESEND, PAID_NOTICES_LIST, UNPAID_NOTICES_LIST, BANK_LIST, OTHER;

    fun getValue() : String {
        return "portal.type." + this.name
    }
}