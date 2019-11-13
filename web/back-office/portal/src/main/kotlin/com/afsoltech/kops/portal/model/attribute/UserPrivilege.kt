package com.nanobnk.epayment.model.attribute

enum class UserPrivilege {
    ROLE_ADMIN, ROLE_USER, PRE_AUTH;

    fun getValue() : String {
        return "portal.privilege." + this.name
    }
}

