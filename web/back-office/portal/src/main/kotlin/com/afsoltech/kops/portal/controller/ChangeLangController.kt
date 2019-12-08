package com.afsoltech.kops.portal.controller

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView
import org.springframework.context.i18n.LocaleContextHolder
import java.util.Locale
import javax.servlet.http.HttpServletResponse


@Controller
class ChangeLangController{

    @GetMapping("/lang")
    fun getInternationalPage(res: HttpServletResponse, model: ModelMap): ModelAndView { //

        val locale = LocaleContextHolder.getLocale() // ?lang=$locale
        val localeToUse = if(locale.language.equals("fr", true)) Locale.FRANCE else Locale.UK
        return ModelAndView("redirect:/portal/?lang=$localeToUse", model);
    }
}