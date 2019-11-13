package com.nanobnk.epayment.administration.utils

import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator


@Component
class FileValidator : Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return FileBucket::class.java.isAssignableFrom(clazz)
    }

    override fun validate(obj: Any, errors: Errors) {
        val file = obj as FileBucket

        file.file?.let {
            if (it.size == 0L)
                errors.rejectValue("file", "missing.file")
        }

    }
}