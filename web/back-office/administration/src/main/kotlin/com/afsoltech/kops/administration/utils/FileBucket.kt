package com.nanobnk.epayment.administration.utils

import org.springframework.web.multipart.MultipartFile

data class FileBucket (
    var file: MultipartFile?=null
)