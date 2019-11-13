package com.nanobnk.epayment.core.backoffice.controller

import com.nanobnk.epayment.model.inbound.UnpaidNoticePortalResponseDto
import io.swagger.models.Model
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.Locale
import javax.servlet.http.HttpServletResponse


@Controller
class ChangeLangController{

    @Autowired
    @Qualifier("localeResolver")
    lateinit var localeResolver: LocaleResolver

    @GetMapping("/lang")
    fun getInternationalPage(res: HttpServletResponse, req: HttpServletRequest, model: ModelMap): ModelAndView { //

//        val auth = SecurityContextHolder.getContext().authentication
//        val model = ModelAndView()
//        model.addObject("username", auth.name)
//        model.addObject("UnpaidNotice", emptyList<UnpaidNoticePortalResponseDto>())
//        model.addObject("parentMenuHighlight", "notices-index")
//        model.addObject("menuHighlight", "notices-unpaid")
//        model.viewName = "user/list-unpaid-customs" //international
//        val localBase = req.locale
        val locale = LocaleContextHolder.getLocale() // ?lang=$locale
//        val lang = res.getHeader(HttpHeaders.ACCEPT_LANGUAGE)
//        val langCont = res.getHeader(HttpHeaders.CONTENT_LANGUAGE)
//        model.addObject("lang", locale)
//        model.addAttribute("language", locale)
//        model.addAttribute(HttpHeaders.ACCEPT_LANGUAGE, locale.language)
//        res.setlHeader(HttpHeaders.ACCEPT_LANGUAGE, locale.language)
//        res.setHeader(HttpHeaders.CONTENT_LANGUAGE, locale.toLanguageTag())
//        res.locale = locale
//
//        localeResolver.setLocale(req, res, locale)
        val prefix = if(req.requestURI.contains("admin")) "admin" else "report"
        val localeToUse = if(locale.language.equals("fr", true)) Locale.FRANCE else Locale("en", "GB") //.UK
        return ModelAndView("redirect:/$prefix?lang=$localeToUse", model);
//        return model
//        return ModelAndView("redirect:/page_fragment", model);
    }
}