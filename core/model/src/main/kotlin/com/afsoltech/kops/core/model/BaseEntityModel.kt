package com.afsoltech.kops.core.model

import com.afsoltech.core.model.attribute.BaseStatus


data class BaseEntityModel (
        var id:Long?=null,
        var code:String?=null,
        var name:String?=null,
        var status:BaseStatus = BaseStatus.INACTIVE,
        var entityId:Int?=null,
        var pageNumber:Int?=null
)