package com.afsoltech.kops.core.model


data class AuthResponseDto (
        val resultCode: String, //?=null
        val resultMsg: String,
        val taxpayerNumber: String,
        val userEmail: String,
        val userCategory: String
)


data class AuthRequestDto (
       val taxpayerNumber: String?=null,
        val userEmail: String?=null,
        val userCategory: String?=null
)