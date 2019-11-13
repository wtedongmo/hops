package com.nanobnk.epayment.core.backoffice.controller

//import org.springframework.boot.autoconfigure.web.ErrorController
import com.nanobnk.util.rest.error.ErrorAttributeHandler
import com.nanobnk.util.rest.error.ErrorController
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.web.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.context.MessageSource
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

//:
@Controller("myCustomErrorController2")
@RequestMapping("/error")
class MyCustomErrorController2 : org.springframework.boot.autoconfigure.web.ErrorController {

    companion object : KLogging()


    fun handleError(): ModelAndView {
        val mav = ModelAndView("error")
        mav.addObject("status", "")
        mav.addObject("message", "Application Error")
        mav.addObject("url", "")
        return mav
    }

    override fun getErrorPath(): String {
        return "error"
    }

}