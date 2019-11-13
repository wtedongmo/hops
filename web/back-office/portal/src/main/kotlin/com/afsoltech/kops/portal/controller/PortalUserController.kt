package com.nanobnk.epayment.portal.controller

import com.nanobnk.epayment.model.attribute.LoginDto
import com.nanobnk.epayment.model.attribute.OtpDto
import com.nanobnk.epayment.model.attribute.UserCategory
import com.nanobnk.epayment.model.attribute.UserPrivilege
import com.nanobnk.epayment.model.inbound.AuthRequestDto
import com.nanobnk.epayment.model.inbound.UnpaidNoticePortalRequestDto
import com.nanobnk.epayment.portal.entity.AppUser
import com.nanobnk.epayment.portal.repository.AppUserRepository
import com.nanobnk.epayment.portal.service.OTPService
import com.nanobnk.epayment.portal.service.PortalCheckUserInfosService
import com.nanobnk.util.rest.error.ErrorController
import com.nanobnk.util.rest.util.enforce
import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


@RestController
class PortalUserController(val portalCheckUserInfosService: PortalCheckUserInfosService, val userRepository: AppUserRepository,
                           val otpService: OTPService) {

    companion object : KLogging()

    @Value("\${spring.application.name}")
    internal var appName: String? = null

    @GetMapping("/", "/login")
    fun loginPage(request: HttpServletRequest, response: HttpServletResponse, @RequestParam(value = "fail", required = false) fail: Boolean?,
                  @RequestParam(value = "logout", required = false) logout: Boolean?,
                  @RequestParam(value = "errorMessage", required = false) errorMessage: String?): ModelAndView {
        val model = ModelAndView()

//        val auth = SecurityContextHolder.getContext().authentication
//        if (auth != null) {
//            val username = auth.name
//
//            //Remove the recently used OTP from server.
//            otpService.clearOTP(username)
//
//            SecurityContextLogoutHandler().logout(request, response, auth)
//        }

        val message = " Welcome to my Page"

        model.addObject("appName", appName)
        model.addObject("message", message)

        fail?.let {
            if(fail)
                model.addObject("loginMessage", "bad.credentials.provided")
        }

        errorMessage?.let {
            model.addObject("errorMessage", errorMessage)
        }
//        logger.info("username: " + auth.name)
        model.addObject("loginForm", LoginDto())
        model.addObject("loginFormLink", "/checkinfos")
        model.viewName = "login"
        return model
        //        return "signin";
    }

    @Throws(Exception::class)
    @PostMapping("/checkinfos") //
    fun checkUserInfoAndGenerateOTP(@ModelAttribute("loginForm") loginForm: LoginDto, request: HttpServletRequest): ModelAndView { //@ResponseBody

        val model = ModelAndView()
//        val error = ErrorController()
        //Call Camcis API to check
        enforce(!loginForm.nui.isNullOrBlank() && !loginForm.email.isNullOrBlank()&& !loginForm.category?.name.isNullOrBlank(),
                listOf("Login.form.empty"))
        val boolNui = loginForm.nui!!.length<10 || loginForm.nui!!.length>20
        val boolEmail = loginForm.email!!.length<10 || loginForm.email!!.length>30
        val boolCategory = !loginForm.category!!.equals(UserCategory.E) && !loginForm.category!!.equals(UserCategory.R)
        if(boolNui || boolEmail  || boolCategory){
            model.addObject("loginMessage", "Bad informations was provided")
            model.viewName = "login"
            return model
        }

        val authRequest = AuthRequestDto(loginForm.nui!!, loginForm.email!!, loginForm.category!!.name)
        var authResponse = portalCheckUserInfosService.checkUserInfos(authRequest)

        var error: String? = null

        val valrandom = Math.random() + 0.3
        if (!authResponse!!.resultCode.equals("S", true)) { // valrandom < 0.5
            // valeur Non OK
//            model.addObject("loginMessage", "Bad informations was provided")
            model.viewName = "redirect:/login?fail=true"
            return model
        } else {
            //valeur OK
            model.addObject("loginMessage", "Good informations")

            var user =userRepository.findByUsername(loginForm.nui + "#" + loginForm.email)
            //Create portal and sign it up, generate otp page
            if(user == null){
                val passwordEncoder = BCryptPasswordEncoder()
                user = AppUser(username = loginForm.nui + "#" + loginForm.email, password = passwordEncoder.encode(loginForm.nui!!),
                        nui = loginForm.nui, email = loginForm.email, privilege = UserPrivilege.ROLE_USER)
                user.category = loginForm.category  //!!.equals("R", true)) UserCategory.R else UserCategory.E
                user = userRepository.save(user)
            }

            try {
                val loggedUser = request.session.getAttribute("loggetUser") as String?
                if(loggedUser.isNullOrBlank()) {
                    request.login(user!!.username, loginForm.nui)
                }
                request.session.setAttribute("loggetUser", user!!.username)
                otpService.generateOTPAndSendMail(user, false)
                model.addObject("useremail", user.email)
                model.addObject("username", user.username)
                model.addObject("otpForm", OtpDto())
                model.addObject("orpFormLink", "/validateOtp")
                model.viewName = "otp/otppage"
                return model
            } catch (e: ServletException) {
                error = e.message
                request.logout()
                logger.error("Error while login ", e)
            }

        }
//        model.addObject("loginMessage", error)
        model.viewName = "redirect:/login?errorMessage=$error"
//        model.addObject("unpaidNoticeForm", UnpaidNoticePortalRequestDto())
//        model.viewName = "portal/unpaid-customs-form"
        return model
    }

    @GetMapping("/portal/homepage")
    fun homePage(): ModelAndView {
        val auth = SecurityContextHolder.getContext().authentication
        logger.info("username: " + auth.name)
        val model = ModelAndView()
        model.addObject("username", auth.name)
        model.viewName = "page_tpl_fragment"
        return model
    }


    @GetMapping("/403")
    fun error403(): ModelAndView {
        return ModelAndView("error/403")
    }

    @GetMapping("/error", "/404", "/500")
    fun error(req: HttpServletRequest, reqRes: HttpServletResponse, @RequestParam(value = "status", required = false) status: String?,
              @RequestParam(value = "message", required = false) message: String?,
              @RequestParam(value = "url", required = false) url: String?): ModelAndView {


        val mav = ModelAndView()
        if(status==null){mav.addObject("status", reqRes.status)}
        else {mav.addObject("status", status)}

        if(message==null) mav.addObject("message", "Application Error")
        else message?.let {mav.addObject("message", message)}

        if(url==null){mav.addObject("url", req.requestURL)}
        else {mav.addObject("url", url)}

        val auth = SecurityContextHolder.getContext().authentication
        val authList =  auth.authorities.map { it.authority }
        if((authList.contains("ROLE_ANONYMOUS") || authList.contains(UserPrivilege.PRE_AUTH.name)) && !authList.contains(UserPrivilege.ROLE_USER.name))
            mav.setViewName("redirect:/login?errorMessage=Reessayez SVP!")
        else
            mav.setViewName("error")

        return mav
    }

    @GetMapping("/logout")
    @ResponseBody
    fun logout(request: HttpServletRequest, response: HttpServletResponse): ModelAndView {

        val auth = SecurityContextHolder.getContext().authentication
        if (auth != null) {
            val username = auth.name

            //Remove the recently used OTP from server.
            otpService!!.clearOTP(username)

            SecurityContextLogoutHandler().logout(request, response, auth)
        }

        return ModelAndView("redirect:/login?logout=true")
    }

}
