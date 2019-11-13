package com.nanobnk.epayment.portal.model

import com.nanobnk.epayment.model.attribute.RequestTypePortal

class SessionLogDto<T, U>(
        var userId: Long?=null,
        var username: String?=null,
        var requestType: RequestTypePortal? = null,
        var request: T?=null,
        var response: U?=null
)