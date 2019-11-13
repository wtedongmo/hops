package com.nanobnk.epayment.model.attribute

import org.hibernate.validator.constraints.Length

data class LoginDto (

        var nui: String?=null,
        var email: String?=null,
        var category: UserCategory?=null
)