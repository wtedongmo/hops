package com.nanobnk.epayment.model.attribute

enum class BankStatus {
    CREATED, ACTIVE, CANCELED, INACTIVE, SUSPENDED;

    fun getValue() : String {
        return "portal.type." + this.name
    }
}