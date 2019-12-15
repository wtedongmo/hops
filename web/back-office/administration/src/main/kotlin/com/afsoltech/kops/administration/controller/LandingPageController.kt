package com.afsoltech.kops.administration.controller

import com.afsoltech.core.backoffice.controller.AbstractBasePagingController
import com.afsoltech.core.backoffice.util.SecurityUtil
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

        //val loggedInUserId = securityUtil.getLoggedInUserId()

        return mav
    }
}