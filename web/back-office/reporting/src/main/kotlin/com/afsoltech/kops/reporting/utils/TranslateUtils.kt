//package com.nanobnk.epayment.reporting.utils
//
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.context.MessageSource
//import org.springframework.context.i18n.LocaleContextHolder
//import org.springframework.stereotype.Component
//
//@Component
//class TranslateUtils {
//
//    @Autowired
//    @Qualifier(value = "messageSource")
//    lateinit var messageSource: MessageSource
//
//    fun translate(codeString: String):String{
//        val locale = LocaleContextHolder.getLocale()
//        return  messageSource.getMessage(codeString, emptyArray(), codeString, locale)
//    }
//
//    fun translateList(list: List<String>): List<String>{
//        val locale = LocaleContextHolder.getLocale()
//
//        return  list.map { code ->
//            messageSource.getMessage(code, emptyArray(), code, locale)
//        }.toList()
//    }
//
//    fun translateMap(mapValue: Map<String, String>): Map<String, String>{
//        val locale = LocaleContextHolder.getLocale()
//
//        return  mapValue.map{ (code, value) ->
//            code to messageSource.getMessage(value, emptyArray(), value, locale)
//        }.toMap()
//    }
//}