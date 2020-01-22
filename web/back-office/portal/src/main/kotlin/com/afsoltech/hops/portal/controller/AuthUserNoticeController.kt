package com.afsoltech.core.controller

import com.afsoltech.core.exception.RestException
import com.afsoltech.core.model.attribute.RequestType
import com.afsoltech.core.service.user.OTPService
import com.afsoltech.core.util.enforce
import com.afsoltech.core.model.OtpDto
import com.afsoltech.hops.core.model.notice.UnpaidNoticeRequestDto
import com.afsoltech.hops.core.model.notice.AuthRequestDto
import com.afsoltech.hops.service.integration.CheckUserInfoService
import com.afsoltech.core.model.attribute.AuthUserCustomsDto
import com.afsoltech.core.model.attribute.CustomsUserCategory
import com.afsoltech.core.service.utils.TranslateUtils
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest

@RestController
@RequestMapping("/portal", "")
class AuthUserNoticeController(val checkUserInfoService: CheckUserInfoService, val otpService: OTPService){
    companion object : KLogging()

    @Autowired
    lateinit var translateUtils: TranslateUtils
    @GetMapping("/", "/auth-customs-user")
    fun unPaidNoticeForm(@RequestParam(value = "error", required = false) error: Boolean?,
                         @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                         request: HttpServletRequest): ModelAndView {

        val model = ModelAndView()

        val auth = SecurityContextHolder.getContext().authentication

        error?.let {
            if (error) model.addObject("errorMessage", "app.custom.auth.bad.params")
        }

        errorMessage?.let {
            model.addObject("errorMessage", errorMessage)
        }
        model.addObject("username", auth.name)
        model.addObject("authCustomsUserForm", AuthUserCustomsDto())
        model.addObject("parentMenuHighlight", "notices-index")
        model.addObject("menuHighlight", "notices-auth")
        model.viewName = "portal/auth-customs-user-form"

        return model
    }


    @PostMapping("/auth-customs-user")
    fun authCustomsUserAndGenerateOTP(@ModelAttribute("authCustomsUserForm") authForm: AuthUserCustomsDto, request: HttpServletRequest): ModelAndView { //@ResponseBody

        try{
            val model = ModelAndView()
            logger.trace { "Params ${authForm.niu}  ${authForm.email}  ${authForm.category}" }
            enforce(!authForm.niu.isNullOrBlank() && !authForm.email.isNullOrBlank() && !authForm.category?.name.isNullOrBlank(),
                    listOf("app.customs.auth.field.required"))

            val boolNui = authForm.niu!!.length<10 || authForm.niu!!.length>20
            val boolEmail = authForm.email!!.length<5 || authForm.email!!.length>100
            val boolCategory = !authForm.category!!.equals(CustomsUserCategory.E) && !authForm.category!!.equals(CustomsUserCategory.R)
            if(boolNui || boolEmail  || boolCategory){
                model.viewName = "redirect:/portal/auth-customs-user?errorMessage=app.customs.auth.bad.params"
                return model
            }

            val authRequest = AuthRequestDto(authForm.niu!!, authForm.email!!, authForm.category!!.name)
            val authResponse = checkUserInfoService.checkUserInfo(authRequest, request)

            var error: String? = null

            val valrandom = Math.random() + 0.3
            if (!authResponse.resultCode.equals("S", true)) { // valrandom < 0.5
                // valeur Non OK
                model.viewName = "redirect:/portal/auth-customs-user?error=true"
                return model
            } else {
                //valeur OK
                model.addObject("loginMessage", "Good informations")
                request.getSession().setAttribute("Auth_Customs", authRequest);
                try {
                    val auth = SecurityContextHolder.getContext().authentication
                    otpService.generateOTPAndSendMail(auth.name, RequestType.AUTH_CUSTOMS, false)
                   model.addObject("message", "app.customs.auth.code.send.mail")
                   model.addObject("otpForm", OtpDto())
    //                model.addObject("otpFormLink", "/validateOtp")
                    model.viewName = "portal/auth-customs-otp-form"
                    return model
                } catch (e: ServletException) {
                    error = e.message
                    request.logout()
                    logger.error("Error while login ", e)
                }

            }
            model.viewName = "redirect:/portal/auth-customs-user?errorMessage=${error?: "Null Exception found"}"
            return model
        }catch (ex: RestException){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/portal/auth-customs-user?errorMessage="+translateUtils.translate(ex.message?:""))
        }catch (ex: Exception){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/portal/auth-customs-user?errorMessage=admin.system.error")
        }
    }


    @GetMapping("/auth-customs-resend-code")
    fun resendOtp(): ModelAndView {

        try{
            val model = ModelAndView()
            val auth = SecurityContextHolder.getContext().authentication
            model.addObject("username", auth.name)
            val username = auth.name

            otpService.generateOTPAndSendMail(username, RequestType.AUTH_CUSTOMS,true)
            model.addObject("otpMessage", "app.customs.auth.otp.code.resend")
            model.addObject("otpForm", OtpDto())
            model.viewName = "portal/auth-customs-otp-form"
            return model
        }catch (ex: Exception){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/portal/auth-customs-user?errorMessage=admin.system.error")
        }
    }

    @PostMapping("/valid-auth-customs-otp")
    fun validateOtp(otpnum: OtpDto, servletRequest: HttpServletRequest): ModelAndView {

        try{
            val model = ModelAndView()
            val auth = SecurityContextHolder.getContext().authentication
            val username = auth.name
            model.addObject("username", auth.name)

            logger.info(" Otp Number : " + otpnum.otpNumber!!)

            //Validate the Otp
            if (otpnum.otpNumber != null) {
                if (otpService!!.validateOTP(username, RequestType.AUTH_CUSTOMS, otpnum.otpNumber!!)) {
                    model.addObject("otpMessage", "app.customs.auth.otp.code.valid") //SUCCESS
                    model.addObject("unpaidNoticeForm", UnpaidNoticeRequestDto())
                    model.viewName = "portal/unpaid-customs-form"
                    servletRequest.getSession().setAttribute("loggedInUser", username);
                    return model
                }
            }

            model.addObject("errorMessage", "app.customs.auth.otp.code.failed") //FAIL
            model.addObject("otpForm", OtpDto())
            model.viewName = "portal/auth-customs-otp-form"
            return model
        }catch (ex: RestException){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/portal/auth-customs-otp-form?errorMessage="+translateUtils.translate(ex.message?:""))
        }catch (ex: Exception){
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/portal/auth-customs-otp-form?errorMessage=admin.system.error")
        }
    }
}