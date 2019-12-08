package com.afsoltech.kops.portal.controller

import com.afsoltech.core.repository.user.UserAppRepository
import com.afsoltech.core.service.OTPService
import com.afsoltech.kops.portal.model.OtpDto
import mu.KLogging
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

import javax.servlet.http.HttpServletRequest

/**
 * @author shrisowdhaman
 * Dec 15, 2017
 */
@RestController
class OtpController(val userRepository: UserAppRepository, val otpService: OTPService) { //, val myEmailService: EmailService

    companion object : KLogging()

//    @Value("\${epayment.portal.email.subject}")
//    lateinit var emailSubject: String

//    @Value("\${epayment.portal.otp.code.valid}")
//    lateinit var SUCCESS: String //RESENDMESSAGE

//    @Value("\${epayment.portal.otp.code.failled}")
//    lateinit var FAIL: String

//    @Value("\${epayment.portal.otp.code.resend}")
//    lateinit var RESENDMESSAGE: String

    @GetMapping("/generateOtp")
    fun generateOtp(): ModelAndView {
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name
        val user = userRepository.findByUsername(username)
        otpService.generateOTPAndSendMail(user!!, false)
        val model = ModelAndView()
        model.addObject("login", user.login)
        model.addObject("otpForm", OtpDto())
        model.viewName = "otp/otppage"
        return model
    }


//    fun generateOtp(portal: AppUser, resend: Boolean) {
//
////        val auth = SecurityContextHolder.getContext().authentication
////        val username = auth.name
//
//        val otp = otpService.generateOTP(portal, resend)
//
//        logger.info("OTP : $otp")
//
//        //Generate The Template to send OTP
//        val template = EmailTemplate("SendOtp.html")
//
//        val replacements = HashMap<String, String>()
//        replacements["portal"] = portal.username!!
//        replacements["otpnum"] = otp.toString()
//
//        val message = template.getTemplate(replacements)
//        //val portal = userRepository.findByUsername(username)
//        myEmailService.sendOtpMessage(portal.email!!, emailSubject, "$message  $otp")
//
//        //return ""
//    }

    @GetMapping("/otp/resend")
    fun resendOtp(): ModelAndView {

        val model = ModelAndView()
        val auth = SecurityContextHolder.getContext().authentication
        model.addObject("username", auth.name)
        val username = auth.name
        val user = userRepository.findByUsername(username)

        user?.let{
            otpService.generateOTPAndSendMail(user,true)
            model.addObject("otpMessage", "bank.portal.otp.code.resend")//RESENDMESSAGE
            model.addObject("otpForm", OtpDto())
            model.addObject("useremail", user.email)
            model.addObject("login", user.login)
            model.addObject("orpFormLink", "/validateOtp")
            model.viewName = "otp/otppage"
            return model
        }
        model.addObject("loginMessage", "User.Not.Found")
        model.viewName = "login"
        return model
    }

    @PostMapping("/validateOtp")
    fun validateOtp(otpnum: OtpDto, servletRequest: HttpServletRequest): ModelAndView { //@ResponseBody

        //		final String SUCCESS = "Entered Otp is valid";

        //		final String FAIL = "Entered Otp is NOT valid. Please Retry with New One!";
        val model = ModelAndView()
        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.name
        model.addObject("username", auth.name)

        logger.info(" Otp Number : " + otpnum.otpNumber!!)

        val user = userRepository.findByUsername(username)

        //Validate the Otp
        if (otpnum.otpNumber != null) {
            if (otpService.validateOTP(user, otpnum.otpNumber!!)) {
                model.addObject("otpMessage", "epayment.portal.otp.code.valid") //SUCCESS

//                val updatedAuthorities = ArrayList<GrantedAuthority>() //auth.getAuthorities()
//                updatedAuthorities.add(SimpleGrantedAuthority(user!!.privilege!!.name)) //add your role here [e.g., new SimpleGrantedAuthority("ROLE_NEW_ROLE")]
//
//                val newAuth = UsernamePasswordAuthenticationToken(auth.principal, auth.credentials, updatedAuthorities)
//                SecurityContextHolder.getContext().authentication = newAuth

                model.addObject("billPaymentValidatedForm", "Success payment of bill")
                model.viewName = "portal/bill-payment-valid-form"

//                servletRequest.getSession().setAttribute("loggedInUser", username);
                return model
            }
        }

        model.addObject("otpMessageError", "epayment.portal.otp.code.failed") //FAIL
        model.addObject("otpForm", OtpDto())
        model.viewName = "otp/otppage"
        return model
    }
}
