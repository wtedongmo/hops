package com.afsoltech.kops.portal.model.attribute

enum class BankStatus {
    CREATED, ACTIVE, CANCELED, INACTIVE, SUSPENDED;

    fun getValue() : String {
        return "portal.type." + this.name
    }
}