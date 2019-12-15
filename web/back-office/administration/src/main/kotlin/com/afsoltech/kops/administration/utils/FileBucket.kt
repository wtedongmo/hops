package com.afsoltech.kops.administration.utils

import org.springframework.web.multipart.MultipartFile

data class FileBucket (
    var file: MultipartFile?=null
)