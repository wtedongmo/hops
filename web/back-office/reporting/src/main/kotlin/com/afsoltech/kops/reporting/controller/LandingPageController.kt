package com.nanobnk.epayment.reporting.controller

import com.nanobnk.epayment.core.administration.controller.AbstractBasePagingController
import com.nanobnk.epayment.core.backoffice.util.SecurityUtil
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView

//@RequestMapping(value = ["/"])
//@RestController
class LandingPageController(val securityUtil: SecurityUtil) : AbstractBasePagingController() {

    @GetMapping
    fun loadLandingPage(@RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        val mav = ModelAndView("landing-page")
        return mav
    }
}