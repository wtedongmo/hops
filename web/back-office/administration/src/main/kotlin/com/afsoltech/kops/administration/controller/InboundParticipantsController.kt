//package com.afsoltech.kops.administration.controller
//
//import com.afsoltech.kops.administration.utils.CheckAuth
//import com.nanobnk.epayment.core.administration.controller.AbstractBasePagingController
//import com.nanobnk.epayment.model.InboundParticipantModel
//import com.nanobnk.epayment.model.OutboundParticipantModel
//import com.nanobnk.epayment.model.SearchModel
//import com.nanobnk.epayment.model.UserModel
//import com.nanobnk.epayment.service.InboundParticipantService
//import com.nanobnk.epayment.service.OutboundParticipantService
//import mu.KLogging
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Sort
//import org.springframework.security.core.context.SecurityContextHolder
//import org.springframework.web.bind.annotation.*
//import org.springframework.web.servlet.ModelAndView
//import javax.servlet.http.HttpServletRequest
//
//@RequestMapping("/admin/inbound-participants")
//@RestController
//class InboundParticipantsController(val inboundParticipantService: InboundParticipantService,
//                                    val outboundParticipantService: OutboundParticipantService) : AbstractBasePagingController() {
//    companion object : KLogging()
//
////    @Autowired
////    lateinit var payment
//    @Autowired
//    lateinit var checkAuth: CheckAuth
//
//    private val showParticipantAddFormField = "showParticipantAddForm"
//    private val showParticipantLinkField = "showLinkForm"
//
//    @GetMapping
//    fun loadInboundParticipantsMainPage(@RequestParam(value = "pageNumber", required = false) pageNumber: Int?,
//                                        @RequestParam(value = "showParticipantAddForm", required = false) showParticipantAddForm: Boolean?,
//                                        @RequestParam(value = "editId", required = false) editId: Long?,
//                                        @RequestParam(value = "linkId", required = false) linkId: Long?,
//                                        @RequestParam(value = "deleteId", required = false) deleteId: Long?,
//                                        @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
//                                        @RequestParam(value = "accessDenied", required = false) accessDenied: Boolean?,
//                                        @RequestParam(value = "success", required = false) success: Boolean?,
//                                        @ModelAttribute("searchForm") searchForm: SearchModel?,
//                                        request: HttpServletRequest
//    ): ModelAndView {
//        val mav = ModelAndView("inbound-participant/inbound-participants")
//
//        mav.addObject(showParticipantAddFormField, showParticipantAddForm ?: false)
//        mav.addObject("inboundParticipant", InboundParticipantModel())
//
//        val hasAuth = checkAuth.hasAuthorization()
//        var deniedOp: Boolean=false
//        editId?.let {
//            if(!hasAuth) deniedOp=true
//            else {
//                mav.addObject(showParticipantAddFormField, true)
//                mav.addObject("inboundParticipant", inboundParticipantService.retrieveParticipant(editId))
//            }
//        }
//
//        linkId?.let {
//            if(!hasAuth) deniedOp=true
//            else {
//                mav.addObject(showParticipantLinkField, true)
//                mav.addObject("selectedParticipant", inboundParticipantService.retrieveParticipant(it))
//
//                val linkedParticipants: List<OutboundParticipantModel> = outboundParticipantService.retrieveAllOutboundParticipantsForInboundParticipant(linkId)
//                val outboundParticipants: MutableList<OutboundParticipantModel> = outboundParticipantService.retrieveAllOutboundParticipants()
//                outboundParticipants.removeAll(linkedParticipants)
//
//                mav.addObject("linkedParticipants", linkedParticipants)
//                mav.addObject("unlinkedParticipants", outboundParticipants)
//            }
//        }
//
//        deleteId?.let {
//            if(!hasAuth) deniedOp=true
//            else {
//                val result = inboundParticipantService.delete(deleteId)
//                if (result.equals(-1L))
//                    mav.addObject("DeleteMessage", "unable.to.delete")
//                else
//                    mav.addObject("DeleteMessage", "success.delete")
//            }
//        }
//
//        if(deniedOp) {
//            mav.addObject("accessDenied", "Access.Denied")
//        }
//        accessDenied?.let {
//            if(accessDenied)
//                mav.addObject("accessDenied", "Access.Denied")
//        }
//        errorMessage?.let {
//            mav.addObject("errorMessage", errorMessage)
//        }
//        success?.let {
//            if(success)
//                mav.addObject("SuccessMessage", "operation.success")
//        }
//
//        val auth = SecurityContextHolder.getContext().authentication
//        var inboundParticipants : Page<InboundParticipantModel>? = null
//        if(editId!=null || linkId!=null || deleteId!=null ){
//            val inboundSession = request.session.getAttribute(auth.name+"_inboundP")
//            if(inboundSession!= null)
//                inboundParticipants = inboundSession as Page<InboundParticipantModel>
//        }
//
//        if(inboundParticipants==null && searchForm != null && !searchForm.column.isNullOrEmpty() && !searchForm.value.isNullOrEmpty()
//                && !searchForm.column.equals("NONE", true) ){
//            try {
//                inboundParticipants = inboundParticipantService.search(PageRequest(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, Sort.Direction.ASC, "participantName"),
//                        searchForm.column!!, searchForm.value!!)
//                request.session.setAttribute(auth.name+"_inboundP", inboundParticipants)
//            }catch(ex: Exception){
//                ProviderController.logger.error { ex.message +"\n"+ex.printStackTrace() }
//                mav.addObject("errorMessage", "app.search.error")
//                inboundParticipants = null
//            }
//        }else if(inboundParticipants==null)
//            inboundParticipants = inboundParticipantService.retrieveAllParticipants(calculateCurrentPage(pageNumber), DEFAULT_PAGE_SIZE)
//
//
//        mav.addObject("searchMap", getSearchFieldMap("app.search.Participant", "app.search.Participant"))
//        mav.addObject("searchForm", SearchModel())
//        inboundParticipants?.let {
//            presetPagingValues(inboundParticipants, mav)
//        }
//
//        mav.addObject("inboundParticipants", inboundParticipants?: ArrayList<InboundParticipantModel>())
//
//        return mav
//    }
//
//    @PostMapping
//    fun saveInboundParticipant(@ModelAttribute("inboundParticipant") inboundParticipantModel: InboundParticipantModel,
//                               @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {
//
//        if(!checkAuth.hasAuthorization()){
//            inboundParticipantModel.participantId?.let {
//                return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&editId=${inboundParticipantModel.participantId}" +
//                        "&showParticipantAddForm=true&errorMessage=admin.user.not.authorized")
//            }
//            return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&showParticipantAddForm=true" +
//                    "&errorMessage=admin.user.not.authorized")
//        }
//        // Check parameter
//        if(inboundParticipantModel.participantCode.isNullOrBlank() || inboundParticipantModel.participantStatus?.name.isNullOrBlank() ||
//                inboundParticipantModel.participantName.isNullOrBlank()){
//            inboundParticipantModel.participantId?.let {
//                return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&editId=" +
//                        "${inboundParticipantModel.participantId}&showParticipantAddForm=true&errorMessage=admin.parameter.not.found")
//            }
//            return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&showParticipantAddForm=true&" +
//                    "errorMessage=admin.parameter.not.found")
//        }
//
//        // Check code if already exists or all code been used
//        inboundParticipantModel.participantId?.let {
//            val participant = inboundParticipantService.findById(inboundParticipantModel.participantId!!)
//
//            participant?.let {
//                if(!inboundParticipantModel.participantCode.equals(participant.participantCode, true)){
//                    val list = inboundParticipantService.paymentRepository.
//                            findByInboundParticipantCode(participant.participantCode!!)
//
//                    if(list.isNotEmpty()){
//                       return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&editId=" +
//                                    "${inboundParticipantModel.participantId}&showParticipantAddForm=true&errorMessage=inbound.code.edit.prohibed")
//                    }
//                }
//            }
//        }
//
//        // Check code if already exists
//        val participant = inboundParticipantService.findByCode(inboundParticipantModel.participantCode!!)
//        participant?.let{
//            if(inboundParticipantModel.participantId==null || (inboundParticipantModel.participantId!=null &&
//                            inboundParticipantModel.participantId!=participant.inboundParticipantId)) {
//                inboundParticipantModel.participantId?.let {
//                    return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&editId=${inboundParticipantModel.participantId}" +
//                            "&showParticipantAddForm=true&errorMessage=inbound.code.exists")
//                }
//                return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber" +
//                        "&showParticipantAddForm=true&errorMessage=inbound.code.exists")
//            }
//        }
//
//        try{
//            inboundParticipantService.save(inboundParticipantModel)
//        }catch (ex: Exception){
////            ex.printStackTrace()
//            logger.error(ex.message, ex)
//            inboundParticipantModel.participantId?.let {
//                return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&editId=${inboundParticipantModel.participantId}" +
//                        "&showParticipantAddForm=true&errorMessage=admin.system.error")
//            }
//            return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber" +
//                    "&showParticipantAddForm=true&errorMessage=admin.system.error")
//        }
//
//        return redirectToInboundParticipantsPage(pageNumber, null, null)
//    }
//
//    private fun redirectToInboundParticipantsPage(pageNumber: Int?, linkId: Long?, participantFeeLinkId: Long?): ModelAndView {
//        var url = "redirect:/admin/inbound-participants"
//
//        pageNumber?.let {
//            url = "$url?pageNumber=$pageNumber"
//
//            linkId?.let {
//                url = "$url&linkId=$linkId"
//            }
//
//            participantFeeLinkId?.let {
//                url = "$url&participantFeeLinkId=$participantFeeLinkId"
//            }
//        }
//
//        return ModelAndView("$url&success=true")
//    }
//
//    @GetMapping("/{linkId}/unlinkParticipant/{participantId}")
//    fun unlinkParticipant(@PathVariable("linkId") linkId: Long,
//                          @PathVariable("participantId") participantId: Long,
//                          @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {
//
//        if(!checkAuth.hasAuthorization()){
//            return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&accessDenied=true")
//        }
//        outboundParticipantService.unlinkOutboundParticipantsFromInboundParticipant(inboundParticipantId = linkId, outboundParticipantIds = arrayOf(participantId))
//        return redirectToInboundParticipantsPage(pageNumber = pageNumber, linkId = linkId, participantFeeLinkId = null)
//
//    }
//
//    @GetMapping("/{linkId}/linkParticipant/{participantId}")
//    fun linkParticipant(@PathVariable("linkId") linkId: Long,
//                        @PathVariable("participantId") participantId: Long,
//                        @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {
//
//        if(!checkAuth.hasAuthorization()){
//            return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&accessDenied=true")
//        }
//        outboundParticipantService.linkOutboundParticipantsToInboundParticipant(inboundParticipantId = linkId, outboundParticipantIds = arrayOf(participantId))
//        return redirectToInboundParticipantsPage(pageNumber = pageNumber, linkId = linkId, participantFeeLinkId = null)
//
//    }
//
//}