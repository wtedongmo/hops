//package com.afsoltech.hops.portal.rest
//
//import com.afsoltech.core.exception.ExpiredException
//import mu.KLogging
//import org.springframework.web.HttpRequestMethodNotSupportedException
//import org.springframework.web.bind.annotation.*
//import org.springframework.web.client.ResourceAccessException
//import org.springframework.web.servlet.ModelAndView
//import org.thymeleaf.exceptions.TemplateProcessingException
//import java.io.IOException
//import javax.servlet.ServletException
//import javax.servlet.http.HttpServletRequest
//import javax.servlet.http.HttpServletResponse
//
//@ControllerAdvice
//class ExceptionHandlingController {
//
//    companion object : KLogging()
//    val DEFAULT_ERROR_VIEW = "error"
//
//    @ExceptionHandler(value = arrayOf(Exception::class, ServletException::class, IOException::class,
//            UninitializedPropertyAccessException::class, ResourceAccessException::class, TemplateProcessingException::class,
//            ExpiredException::class, HttpRequestMethodNotSupportedException::class))
//    @Throws(Exception::class, ServletException::class, IOException::class, UninitializedPropertyAccessException::class,
//            ResourceAccessException::class, TemplateProcessingException::class, ExpiredException::class, HttpRequestMethodNotSupportedException::class)
//    fun defaultErrorHandler(req: HttpServletRequest, reqRes: HttpServletResponse, e: Exception): ModelAndView {
//        // If the exception is annotated with @ResponseStatus rethrow it and let
//        // the framework handle it - like the OrderNotFoundException example
//        // at the start of this post.
//        // AnnotationUtils is a Spring Framework utility class.
////        if (AnnotationUtils.findAnnotation(e.javaClass, ResponseStatus::class.java) != null)
////            throw e
//
//        logger.error { e.message + "\n" +e.printStackTrace() }
//        // Otherwise setup and send the portal to a default error-view.
//        val mav = ModelAndView()
//        mav.addObject("status", reqRes.status)
//        mav.addObject("error", e.message)
//        mav.addObject("message", "Application error")
////        mav.addObject("message", e)
////        mav.addObject("exception", e)
//        mav.addObject("url", req.getRequestURL())
//
////        val auth = SecurityContextHolder.getContext().authentication
////        if(auth!=null && !auth.principal.toString().equals("ANONYMOUSUSER", true))
//        mav.setViewName(DEFAULT_ERROR_VIEW)
////        else
////        mav.setViewName("redirect:/error?status=${reqRes.status}&message='Application Error'&url=${req.getRequestURL()}")
//
//        return mav
//    }
//}