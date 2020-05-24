//package com.afsoltech.epayment.portal.rest
//
//import com.afsoltech.epayment.model.attribute.BankDto
//import com.afsoltech.epayment.model.attribute.BankStatus
//import com.afsoltech.epayment.portal.repository.ParticipantRepository
//import mu.KLogging
//import org.springframework.role.core.context.SecurityContextHolder
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.RestController
//import org.springframework.web.servlet.ModelAndView
//
//@RestController
//class ParticipantPartnerController(val participantRepository: ParticipantRepository) {
//
//    companion object : KLogging()
//
//    @GetMapping("/portal/list-partner")  //
//    fun getPartnerList(): ModelAndView {
//        val auth = SecurityContextHolder.getContext().authentication
//
////        val participantList = participantRepository.findByParticipantStatusAndPaymentLinkIsNotNull(BankStatus.ACTIVE.name)
//        val participantList = participantRepository.findAll()
//        val list = participantList.map { participant ->
//            BankDto(participant.participantCode, participant.participantName!!, participant.paymentLink!!) }
//
//        val modelAndView = ModelAndView()
//        modelAndView.addObject("username", auth.name)
//        modelAndView.addObject("Partner", list)
//        // menu highlight
//        modelAndView.addObject("parentMenuHighlight", "bank-index")
//        modelAndView.addObject("menuHighlight", "bank-list")
//        modelAndView.viewName = "portal/list-partner"
//        return modelAndView
//    }
//}