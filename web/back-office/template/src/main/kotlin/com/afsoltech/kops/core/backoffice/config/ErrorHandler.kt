package com.nanobnk.epayment.core.backoffice.config

import mu.KLogging
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.io.IOException


@Component
class ErrorHandler : AccessDeniedHandler {

    companion object : KLogging()

    @Throws(IOException::class, ServletException::class)
    override fun handle(request: HttpServletRequest, response: HttpServletResponse, arg2: AccessDeniedException) {

        val auth = SecurityContextHolder.getContext().authentication

        if (auth != null) {
            logger.info("User '" + auth.name
                    + "' attempted to access the protected URL: "
                    + request.requestURI)
        }

        response.sendRedirect(request.contextPath + "/error/error")

    }

}
