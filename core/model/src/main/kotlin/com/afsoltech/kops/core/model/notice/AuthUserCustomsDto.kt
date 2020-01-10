package com.afsoltech.core.model.attribute

import org.hibernate.validator.constraints.Length

data class AuthUserCustomsDto (

        var niu: String?=null,
        var email: String?=null,
        var category: CustomsUserCategory?=null
)

enum class CustomsUserCategory {
    E, R;

    fun getValue() : String {
        return "customs.user.tyepe." + this.name
    }
}
