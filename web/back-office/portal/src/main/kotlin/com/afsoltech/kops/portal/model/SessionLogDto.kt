package com.afsoltech.kops.portal.model

import com.afsoltech.kops.portal.model.attribute.RequestTypePortal


class SessionLogDto<T, U>(
        var userId: Long?=null,
        var username: String?=null,
        var requestType: RequestTypePortal? = null,
        var request: T?=null,
        var response: U?=null
)