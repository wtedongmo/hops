package com.nanobnk.epayment.model.attribute

enum class UserCategory {
    E, R;

    fun getValue() : String {
        return "portal.privilege." + this.name
    }
}

