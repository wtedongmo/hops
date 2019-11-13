package com.nanobnk.epayment.administration.controller

import com.nanobnk.epayment.administration.utils.CheckAuth
import com.nanobnk.epayment.core.administration.controller.AbstractBasePagingController
import com.nanobnk.epayment.model.InboundParticipantModel
import com.nanobnk.epayment.model.OutboundParticipantModel
import com.nanobnk.epayment.model.OutboundUserApiAuthModel
import com.nanobnk.epayment.service.InboundParticipantService
import com.nanobnk.epayment.service.OutboundParticipantService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@RequestMapping("/admin/outbound-participants")
@RestController
class OutboundParticipantsController(val outboundParticipantService: OutboundParticipantService, val inboundParticipantService: InboundParticipantService) : AbstractBasePagingController() {
    companion object : KLogging()

    private val showParticipantAddFormField = "showParticipantAddForm"
    private val showParticipantLinkField = "showParticipantLinkForm"
    private val showOutboundUserAPIForm = "showOutboundUserAPIForm"

    @Autowired
    lateinit var checkAuth: CheckAuth

    @GetMapping
    fun loadOutboundParticipantsMainPage(@RequestParam(value = "pageNumber", required = false) pageNumber: Int?,
                                         @RequestParam(value = "showParticipantAddForm", required = false) showParticipantAddForm: Boolean?,
                                         @RequestParam(value = "editId", required = false) editId: Long?,
                                         @RequestParam(value = "outAuthId", required = false) outAuthId: Long?,
                                         @RequestParam(value = "linkId", required = false) linkId: Long?,
                                         @RequestParam(value = "deleteId", required = false) deleteId: Long?,
                                         @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                                         @RequestParam(value = "accessDenied", required = false) accessDenied: Boolean?
    ): ModelAndView {
        val mav = ModelAndView("outbound-participant/outbound-participants")

        mav.addObject(showParticipantAddFormField, showParticipantAddForm ?: false)
        mav.addObject("outboundParticipant", OutboundParticipantModel())
        mav.addObject("outboundUserApiAuth", OutboundUserApiAuthModel())

        val hasAuth = checkAuth.hasAuthorization()
        var deniedOp: Boolean=false
        editId?.let {
            if(!hasAuth) deniedOp=true
            else {
                mav.addObject(showParticipantAddFormField, true)
                mav.addObject("outboundParticipant", outboundParticipantService.findOutboundParticipantById(editId))
            }
        }


        outAuthId?.let {
            if(!hasAuth) deniedOp=true
            else {
                mav.addObject(showOutboundUserAPIForm, true)
                mav.addObject("outboundUserApiAuth", outboundParticipantService.getOutBoundUserAPIParams(outAuthId))
            }
        }

        deleteId?.let {
            if(!hasAuth) deniedOp=true
            else {
                val result = outboundParticipantService.delete(deleteId)
                if (result.equals(-1L))
                    mav.addObject("DeleteMessage", "unable.to.delete")
                else
                    mav.addObject("DeleteMessage", "success.delete")
            }
        }

        linkId?.let {
            if(!hasAuth) deniedOp=true
            else {
                mav.addObject(showParticipantLinkField, true)
                mav.addObject("selectedParticipant", outboundParticipantService.findOutboundParticipantById(linkId))

                val linkedParticipants: List<InboundParticipantModel> = outboundParticipantService.retrieveAllInboundParticipantsForOutboundParticipant(linkId)
                val inboundParticipants = inboundParticipantService.retrieveAllParticipants()
                inboundParticipants.removeAll(linkedParticipants)

                mav.addObject("linkedParticipants", linkedParticipants)
                mav.addObject("unlinkedParticipants", inboundParticipants)
            }
        }

        if(deniedOp) {
            mav.addObject("accessDenied", "Access.Denied")
        }
        accessDenied?.let {
            if(accessDenied)
                mav.addObject("accessDenied", "Access.Denied")
        }

        errorMessage?.let {
            mav.addObject("errorMessage", errorMessage)
        }

        val outboundParticipants = outboundParticipantService.retrieveAllOutboundParticipants(calculateCurrentPage(pageNumber), DEFAULT_PAGE_SIZE)

        outboundParticipants.let {
            presetPagingValues(outboundParticipants, mav)
        }

        mav.addObject("outboundParticipants", outboundParticipants)

        return mav
    }

    @PostMapping
    fun saveOutboundParticipant(@ModelAttribute("outboundParticipant") outboundParticipantModel: OutboundParticipantModel,
                                @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        if(!checkAuth.hasAuthorization()){
            outboundParticipantModel.outboundParticipantId?.let {
                return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&editId=${outboundParticipantModel.outboundParticipantId}" +
                        "&showParticipantAddForm=true&errorMessage=admin.user.not.authorized")
            }
            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&showParticipantAddForm=true" +
                    "&errorMessage=admin.user.not.authorized")
        }
        // Check parameter
        if(outboundParticipantModel.participantCode.isNullOrBlank() || outboundParticipantModel.participantStatus?.name.isNullOrBlank() ||
                outboundParticipantModel.participantName.isNullOrBlank()){
            outboundParticipantModel.outboundParticipantId?.let {
                return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&editId=" +
                        "${outboundParticipantModel.outboundParticipantId}&showParticipantAddForm=true&errorMessage=admin.parameter.not.found")
            }
            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&showParticipantAddForm=true" +
                    "&errorMessage=admin.parameter.not.found")
        }

        // Check code if already exists or all code been used
//        outboundParticipantModel.outboundParticipantId?.let {
//            val participant = outboundParticipantService.findById(it)
//
//            participant?.let {
//                if(!outboundParticipantModel.participantCode.equals(participant.participantCode, true)){
//                    val list = inboundParticipantService.paymentRepository.
//                            findByInboundParticipantCode(outboundParticipantModel.participantCode!!)
//
//                    if(list.isNotEmpty()){
//                        return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&editId=" +
//                                "${outboundParticipantModel.outboundParticipantId}&showParticipantAddForm=true&errorMessage=inbound.code.edit.prohibed")
//                    }
//                }
//            }
//        }

        // Check code if already exists
        val participant = outboundParticipantService.findByCode(outboundParticipantModel.participantCode!!)
        participant?.let{
            if(outboundParticipantModel.outboundParticipantId==null || (outboundParticipantModel.outboundParticipantId!=null &&
                            outboundParticipantModel.outboundParticipantId!=participant.outboundParticipantId)) {
                outboundParticipantModel.outboundParticipantId?.let {
                    return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&editId=${outboundParticipantModel.outboundParticipantId}" +
                            "&showParticipantAddForm=true&errorMessage=inbound.code.exists")
                }
                return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber" +
                        "&showParticipantAddForm=true&errorMessage=inbound.code.exists")
            }
        }

        try{
            if (outboundParticipantModel.outboundParticipantId != null) {
                outboundParticipantService.updateOutboundParticipant(checkNotNull(outboundParticipantModel.outboundParticipantId), outboundParticipantModel)
            } else {
                outboundParticipantService.save(outboundParticipantModel)
            }
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            outboundParticipantModel.outboundParticipantId?.let {
                return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&editId=${outboundParticipantModel.outboundParticipantId}" +
                        "&showParticipantAddForm=true&errorMessage=admin.system.error")
            }
            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber" +
                    "&showParticipantAddForm=true&errorMessage=admin.system.error")
        }

        return redirectToOutboundParticipantsPage(pageNumber, null)
    }

    @PostMapping("/auth-api")
    fun saveOutboundUserAPIParams(@ModelAttribute("outboundUserApiAuth") outboundUserApiAuth: OutboundUserApiAuthModel,
                                @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        if(!checkAuth.hasAuthorization()){
            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&outAuthId=${outboundUserApiAuth.outboundParticipantId}" +
                    "&errorMessage=admin.user.not.authorized")
        }
        // Check parameter
        if(outboundUserApiAuth.outboundParticipantId==null || outboundUserApiAuth.userID.isNullOrBlank() ||
                outboundUserApiAuth.userPasspharse.isNullOrBlank() || outboundUserApiAuth.userPassword.isNullOrBlank()){
            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&outAuthId=${outboundUserApiAuth.outboundParticipantId}" +
                    "&errorMessage=admin.parameter.not.found")
        }


        try{
            outboundParticipantService.setOutBoundUserAPIParams(outboundUserApiAuth)
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&outAuthId=${outboundUserApiAuth.outboundParticipantId}" +
                    "&errorMessage=admin.system.error")
        }

        return redirectToOutboundParticipantsPage(pageNumber, null)
    }

    private fun redirectToOutboundParticipantsPage(pageNumber: Int?, linkId: Long?): ModelAndView {
        var url = "redirect:/admin/outbound-participants"

        pageNumber?.let {
            url = "$url?pageNumber=$pageNumber"

            linkId?.let {
                url = "$url&linkId=$linkId"
            }
        }

        return ModelAndView(url)
    }

    @GetMapping("/admin/{linkId}/unlinkParticipant/{participantId}")
    fun unlinkParticipant(@PathVariable("linkId") linkId: Long,
                          @PathVariable("participantId") participantId: Long,
                          @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        if(!checkAuth.hasAuthorization()){
            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&accessDenied=true")
        } else {
            outboundParticipantService.unlinkOutboundParticipantsFromInboundParticipant(inboundParticipantId = participantId,
                    outboundParticipantIds = arrayOf(linkId))
            return redirectToOutboundParticipantsPage(pageNumber = pageNumber, linkId = linkId)
        }
    }

    @GetMapping("/admin/{linkId}/linkParticipant/{participantId}")
    fun linkParticipant(@PathVariable("linkId") linkId: Long,
                        @PathVariable("participantId") participantId: Long,
                        @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        if(!checkAuth.hasAuthorization()){
            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&accessDenied=true")
        } else {
            outboundParticipantService.linkOutboundParticipantsToInboundParticipant(inboundParticipantId = participantId,
                    outboundParticipantIds = arrayOf(linkId))
            return redirectToOutboundParticipantsPage(pageNumber = pageNumber, linkId = linkId)
        }

    }
}