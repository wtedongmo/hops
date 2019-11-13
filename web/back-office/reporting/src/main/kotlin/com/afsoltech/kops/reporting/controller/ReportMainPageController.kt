package com.nanobnk.epayment.reporting.controller

import com.nanobnk.epayment.core.backoffice.util.SecurityUtil
import com.nanobnk.epayment.model.attribute.BaseStatus
import com.nanobnk.epayment.model.attribute.UserType
import com.nanobnk.epayment.reporting.utils.PaidNoticeReportModel
import com.nanobnk.epayment.repository.IssuerOfficeRepository
import com.nanobnk.epayment.repository.UserRepository
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@RestController
@RequestMapping("/", "/report")
class ReportMainPageController (val securityUtil: SecurityUtil,
                                val userRepository: UserRepository ) {

    companion object : KLogging()

    @Autowired
    lateinit var issuerOfficeRepository: IssuerOfficeRepository

    @Value("\${internal.epayment.reporting.zoneid}")
    var zoneIdValue : String = ""

//    @Autowired
//    @Qualifier("localeResolver")
//    lateinit var localeResolver: LocaleResolver

    @GetMapping
    fun loadMainPage(res: HttpServletResponse, req: HttpServletRequest): ModelAndView {

        val auth = SecurityContextHolder.getContext().authentication
//        if(auth==null){
//            val mav = ModelAndView("login")
//            val LOGIN_FORM = "loginForm"
//            mav.addObject(LOGIN_FORM, UserLoginView())
//            return mav
//        }
        logger.trace { "loading paid customs reports" }
        val reportModel = PaidNoticeReportModel()
        val mav = ModelAndView("report/paid-customs-main-page-report")
        mav.addObject("paidnotice", "paidnotice")

        val username = auth.principal.toString()
        val user = userRepository.findByUsername(username)
        if(user!=null && user.type!!.equals(UserType.PARTICIPANT)){
            mav.addObject("participant", "participant")
            reportModel.reportCode=115
        }else if(user!=null && user.type!!.equals(UserType.PROVIDER)){
            mav.addObject("provider", "provider")
            reportModel.reportCode=117
        }else{
            mav.addObject("otherUser", "otherUser")
            val offices = issuerOfficeRepository.findByStatus(BaseStatus.ACTIVE).map { it -> it.code }
            mav.addObject("offices", offices)
        }



        mav.addObject("PaidNoticeModel", reportModel)

//        addAssociatedParticipants(mav)

//        val locale = LocaleContextHolder.getLocale() // ?lang=$locale
//        if(locale.language.equals("fr", true)) {
////            (localeResolver as SessionLocaleResolver).setDefaultLocale(Locale.FRANCE)
//            (localeResolver as CookieLocaleResolver).setDefaultLocale(Locale.FRANCE)
//        }else
//            localeResolver.setLocale(req, res, locale)
//        return ModelAndView("redirect:/", model);
        return mav

    }

//    private fun addAssociatedParticipants(mav: ModelAndView) {
//        val loggedInUser = securityUtil.getLoggedInUser()
//        addInboundParticipants(mav, loggedInUser)
//        addOutboundParticipants(mav, loggedInUser)
//    }
//
//    private fun addOutboundParticipants(mav: ModelAndView, loggedInUser: UserEntity) {
//        mav.addObject("associatedOutboundParticipants", outboundParticipantService.getAssociatedOutboundParticipants(loggedInUser))
//
//    }
//
//    private fun addInboundParticipants(mav: ModelAndView, loggedInUser: UserEntity) {
//        mav.addObject("associatedInboundParticipants", inboundParticipantService.getAssociatedInboundParticipants(loggedInUser))
//    }

}