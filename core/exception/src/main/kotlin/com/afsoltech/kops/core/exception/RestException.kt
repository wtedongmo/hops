package com.afsoltech.core.exception

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import java.util.*

open class RestException(
        val httpStatus: HttpStatus,
        message: String?,
        val parameters: Collection<Any> = emptyList(),
        val locale: Locale = LocaleContextHolder.getLocale()
) : RuntimeException(message)

class UnauthorizedException(message: String?, parameters: Collection<Any> = emptyList(), locale: Locale = LocaleContextHolder.getLocale()) : RestException(httpStatus = HttpStatus.UNAUTHORIZED, message = message, parameters = parameters, locale = locale)

class ForbiddenException(message: String?, parameters: Collection<Any> = emptyList(), locale: Locale = LocaleContextHolder.getLocale()) : RestException(httpStatus = HttpStatus.FORBIDDEN, message = message, parameters = parameters, locale = locale)

class NotFoundException(message: String?, parameters: Collection<Any> = emptyList(), locale: Locale = LocaleContextHolder.getLocale()) : RestException(httpStatus = HttpStatus.NOT_FOUND, message = message, parameters = parameters, locale = locale)

class BadRequestException(message: String?, parameters: Collection<Any> = emptyList(), locale: Locale = LocaleContextHolder.getLocale()) : RestException(httpStatus = HttpStatus.BAD_REQUEST, message = message, parameters = parameters, locale = locale)

class ConflictException(message: String?, parameters: Collection<Any> = emptyList(), locale: Locale = LocaleContextHolder.getLocale()) : RestException(httpStatus = HttpStatus.CONFLICT, message = message, parameters = parameters, locale = locale)

class GeneralException(message: String?, parameters: Collection<Any> = emptyList(), locale: Locale = LocaleContextHolder.getLocale()) : RestException(httpStatus = HttpStatus.INTERNAL_SERVER_ERROR, message = message, parameters = parameters, locale = locale)

class ExpiredException(message: String?, parameters: Collection<Any> = emptyList(), locale: Locale = LocaleContextHolder.getLocale()) : RestException(httpStatus = HttpStatus.PRECONDITION_FAILED, message = message, parameters = parameters, locale = locale)