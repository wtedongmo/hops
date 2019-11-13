//package com.nanobnk.epayment.portal.controller
//
//import com.nanobnk.epayment.model.attribute.BankDto
//import com.nanobnk.epayment.model.attribute.BankStatus
//import com.nanobnk.epayment.portal.repository.BankRepository
//import mu.KLogging
//import org.springframework.security.core.context.SecurityContextHolder
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.RestController
//import org.springframework.web.servlet.ModelAndView
//
//@RestController
//class BankController(val bankRepository: BankRepository) {
//
//    companion object : KLogging()
//
//    @GetMapping("/portal/list-bank")  //
//    fun getBankList(): ModelAndView {
//        val auth = SecurityContextHolder.getContext().authentication
//
//        val bankList = bankRepository.findByBankStatus(BankStatus.ACTIVE)
//        val list = bankList.map { bank ->
//            BankDto(bank.bankCode, bank.bankName!!, bank.bankLink!!) }
//
//        val modelAndView = ModelAndView()
//        modelAndView.addObject("username", auth.name)
//        modelAndView.addObject("Bank", list)
//        // menu highlight
//        modelAndView.addObject("parentMenuHighlight", "bank-index")
//        modelAndView.addObject("menuHighlight", null)
//        modelAndView.viewName = "portal/list-bank"
//        return modelAndView
//    }
//}