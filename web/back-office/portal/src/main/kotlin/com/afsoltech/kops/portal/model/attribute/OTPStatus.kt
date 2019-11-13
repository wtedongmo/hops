package com.nanobnk.epayment.model.attribute

enum class OTPStatus {
    CREATED, USED, CANCELED, EXPIRED;

    fun getValue() : String {
        return "portal.type." + this.name
    }
}