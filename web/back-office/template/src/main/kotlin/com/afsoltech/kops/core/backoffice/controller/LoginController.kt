package com.nanobnk.epayment.core.controller

import com.nanobnk.epayment.model.attribute.UserPrivilege
import mu.KLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.ui.Model
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import org.springframework.web.servlet.View
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
class LoginAPI {

    companion object : KLogging()
    private val LOGIN_FORM = "loginForm"

    @GetMapping("/login") //, "/"
    fun showLoginPage(@RequestParam(value = "error", required = false) error: Boolean?,
                      @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                      request: HttpServletRequest, response: HttpServletResponse): ModelAndView {

//        val auth = SecurityContextHolder.getContext().authentication
//        if (auth != null && !auth.name.equals("anonymousUser", true)) {
//            val username = auth.name
//
//             SecurityContextLogoutHandler().logout(request, response, auth)
//        }
//        val uri = req.requestURL
        val mav = ModelAndView("login")

        error?.let { error ->
            if (error) mav.addObject("errorMessage", "bad.credentials")
        }

        errorMessage?.let {
            mav.addObject("errorMessage", errorMessage)
        }

        mav.addObject(LOGIN_FORM, UserLoginView())

        return mav
    }

    @GetMapping("/login_fail")
    fun showLoginPageFail(@RequestParam(value = "error", required = false) error: Boolean?): ModelAndView {
        val mav = ModelAndView("login")
        mav.addObject("errorMessage", "bad.credentials")
        mav.addObject(LOGIN_FORM, UserLoginView())

        return mav
    }

    @GetMapping("/404", "/500", "/403", "/error", "/error/error")
    fun error(req: HttpServletRequest, reqRes: HttpServletResponse,
              @RequestParam(value = "status", required = false) status: String?,
              @RequestParam(value = "message", required = false) message: String?,
              @RequestParam(value = "url", required = false) url: String?, model: ModelMap): ModelAndView {
        //, @ModelAttribute(value = "loginForm") loginForm: UserLoginView?
        val mav = ModelAndView()
        if(status==null){mav.addObject("status", reqRes.status)}
        else {mav.addObject("status", status)}

        if(message==null) mav.addObject("message", "Application Error")
        else message?.let {mav.addObject("message", message)}

        if(url==null){mav.addObject("url", req.requestURL)}
        else {mav.addObject("url", url)}

        val auth = SecurityContextHolder.getContext().authentication
        val authList =  auth.authorities.map { it.authority }
        if((auth.name.equals("anonymousUser") || authList.contains("ROLE_ANONYMOUS"))){
            val username = model.get("username") as String?
            val password = model.get("password") as String?

            try {
                username?.let {
                    req.login(username, password)
                    val prefix = if(req.requestURI.contains("admin")) "admin" else "report"
                    mav.viewName = "redirect:/$prefix"
                    return mav
                }
            }catch (ex: Exception){
                logger.error { ex.message +"\n"+ ex.printStackTrace() }
            }
            mav.setViewName("redirect:/login?errorMessage=admin.login.try.again")
//        if(auth==null || ) {
//            mav.addObject("errorMessage", "admin.login.try.again")
//            mav.addObject(LOGIN_FORM, UserLoginView())
        }else
            mav.setViewName("error")

        return mav
    }

}

data class UserLoginView(
        val username: String? = null,
        val password: String? = null
)