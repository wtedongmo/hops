package com.afsoltech.kops.administration.controller

import com.afsoltech.core.backoffice.controller.AbstractBasePagingController
import com.afsoltech.core.model.ParticipantModel
import com.afsoltech.core.model.ProviderModel
import com.afsoltech.core.model.SearchModel
import com.afsoltech.core.service.ProviderService
import com.afsoltech.kops.administration.utils.CheckAuth
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

@RequestMapping("/admin/provider")
@RestController
class ProviderController(val providerService: ProviderService) : AbstractBasePagingController() {
    companion object : KLogging()

    private val showParticipantAddFormField = "showParticipantAddForm"
    private val showParticipantLinkField = "showParticipantLinkForm"
    private val showProviderUserAPIForm = "showProviderUserAPIForm"

    @Autowired
    lateinit var checkAuth: CheckAuth

    @GetMapping
    fun loadProvidersMainPage(@RequestParam(value = "pageNumber", required = false) pageNumber: Int?,
                                         @RequestParam(value = "showParticipantAddForm", required = false) showParticipantAddForm: Boolean?,
                                         @RequestParam(value = "editId", required = false) editId: Long?,
                                         @RequestParam(value = "outAuthId", required = false) outAuthId: Long?,
                                         @RequestParam(value = "linkId", required = false) linkId: Long?,
                                         @RequestParam(value = "deleteId", required = false) deleteId: Long?,
                                         @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                                         @RequestParam(value = "accessDenied", required = false) accessDenied: Boolean?,
                                         @RequestParam(value = "success", required = false) success: Boolean?,
                                         @ModelAttribute("searchForm") searchForm: SearchModel?,
                                         request: HttpServletRequest
    ): ModelAndView {
        val mav = ModelAndView("outbound-participant/outbound-participants")

        mav.addObject(showParticipantAddFormField, showParticipantAddForm ?: false)
        mav.addObject("provider", ProviderModel())
//        mav.addObject("outboundUserApiAuth", ProviderUserApiAuthModel())

        val hasAuth = checkAuth.hasAuthorization()
        var deniedOp: Boolean=false
        editId?.let {
            if(!hasAuth) deniedOp=true
            else {
                mav.addObject(showParticipantAddFormField, true)
                mav.addObject("provider", providerService.findProviderById(editId))
            }
        }


        outAuthId?.let {
            if(!hasAuth) deniedOp=true
            else {
                mav.addObject(showProviderUserAPIForm, true)
//                mav.addObject("providerUserApiAuth", providerService.getOutBoundUserAPIParams(outAuthId))
            }
        }

        deleteId?.let {
            if(!hasAuth) deniedOp=true
            else {
                val result = providerService.delete(deleteId)
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
                mav.addObject("selectedParticipant", providerService.findProviderById(linkId))

//                val linkedParticipants: List<ParticipantModel> = providerService.retrieveAllParticipantsForProvider(linkId)
//                val inboundParticipants = participantService.retrieveAllParticipants()
//                inboundParticipants.removeAll(linkedParticipants)

//                mav.addObject("linkedParticipants", linkedParticipants)
//                mav.addObject("unlinkedParticipants", inboundParticipants)
            }
        }

        if(deniedOp) {
            mav.addObject("accessDenied", "Access.Denied")
        }
        accessDenied?.let {
            if(accessDenied)
                mav.addObject("accessDenied", "Access.Denied")
        }
        success?.let {
            if(success)
                mav.addObject("SuccessMessage", "operation.success")
        }
        errorMessage?.let {
            mav.addObject("errorMessage", errorMessage)
        }

        val auth = SecurityContextHolder.getContext().authentication
        var providers : Page<ProviderModel>? = null
        if(editId!=null || linkId!=null || deleteId!=null || outAuthId!=null){
            val outboundSession = request.session.getAttribute(auth.name+"_outboundP")
            if(outboundSession!= null)
                providers = outboundSession as Page<ProviderModel>
        }

        if(providers==null && searchForm != null && !searchForm.column.isNullOrEmpty() && !searchForm.value.isNullOrEmpty()
                && !searchForm.column.equals("NONE", true) ){
            try {
                providers = providerService.search(PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, Sort.Direction.ASC, "participantName"),
                        searchForm.column!!, searchForm.value!!)
                request.session.setAttribute(auth.name+"_outboundP", providers)
            }catch(ex: Exception){
                logger.error { ex.message +"\n"+ex.printStackTrace() }
                mav.addObject("errorMessage", "app.search.error")
                providers = null
            }
        }else if(providers==null)
            providers = providerService.retrieveAllProviders(calculateCurrentPage(pageNumber), DEFAULT_PAGE_SIZE)


        mav.addObject("searchMap", getSearchFieldMap("app.search.Provider", "app.search.Provider"))
        mav.addObject("searchForm", SearchModel())

        providers?.let {
            presetPagingValues(providers, mav)
        }

        mav.addObject("providers", providers?: ArrayList<ProviderModel>())

        return mav
    }

    @PostMapping
    fun saveProvider(@ModelAttribute("provider") providerModel: ProviderModel,
                                @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        if(!checkAuth.hasAuthorization()){
            providerModel.providerId?.let {
                return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&editId=${providerModel.providerId}" +
                        "&showParticipantAddForm=true&errorMessage=admin.user.not.authorized")
            }
            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&showParticipantAddForm=true" +
                    "&errorMessage=admin.user.not.authorized")
        }
        // Check parameter
        if(providerModel.participantCode.isNullOrBlank() || providerModel.status?.name.isNullOrBlank() ||
                providerModel.participantName.isNullOrBlank()){
            providerModel.providerId?.let {
                return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&editId=" +
                        "${providerModel.providerId}&showParticipantAddForm=true&errorMessage=admin.parameter.not.found")
            }
            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&showParticipantAddForm=true" +
                    "&errorMessage=admin.parameter.not.found")
        }

        // Check code if already exists or all code been used
//        providerModel.providerId?.let {
//            val participant = providerService.findById(it)
//
//            participant?.let {
//                if(!providerModel.participantCode.equals(participant.participantCode, true)){
//                    val list = participantService.paymentRepository.
//                            findByInboundParticipantCode(providerModel.participantCode!!)
//
//                    if(list.isNotEmpty()){
//                        return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&editId=" +
//                                "${providerModel.providerId}&showParticipantAddForm=true&errorMessage=inbound.code.edit.prohibed")
//                    }
//                }
//            }
//        }

        // Check code if already exists
        val participant = providerService.findProviderByCode(providerModel.participantCode!!)
        participant?.let{
            if(providerModel.providerId==null || (providerModel.providerId!=null &&
                            providerModel.providerId!=participant.providerId)) {
                providerModel.providerId?.let {
                    return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber&editId=${providerModel.providerId}" +
                            "&showParticipantAddForm=true&errorMessage=inbound.code.exists")
                }
                return ModelAndView("redirect:/admin/inbound-participants?pageNumber=$pageNumber" +
                        "&showParticipantAddForm=true&errorMessage=inbound.code.exists")
            }
        }

        try{
            if (providerModel.providerId != null) {
                providerService.updateProvider(checkNotNull(providerModel.providerId), providerModel)
            } else {
                providerService.save(providerModel)
            }
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            providerModel.providerId?.let {
                return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&editId=${providerModel.providerId}" +
                        "&showParticipantAddForm=true&errorMessage=admin.system.error")
            }
            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber" +
                    "&showParticipantAddForm=true&errorMessage=admin.system.error")
        }

        return redirectToProvidersPage(pageNumber, null)
    }

//    @PostMapping("/auth-api")
//    fun saveOutboundUserAPIParams(@ModelAttribute("outboundUserApiAuth") outboundUserApiAuth: OutboundUserApiAuthModel,
//                                @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {
//
//        if(!checkAuth.hasAuthorization()){
//            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&outAuthId=${outboundUserApiAuth.providerId}" +
//                    "&errorMessage=admin.user.not.authorized")
//        }
//        // Check parameter
//        if(outboundUserApiAuth.providerId==null || outboundUserApiAuth.userID.isNullOrBlank() ||
//                outboundUserApiAuth.userPasspharse.isNullOrBlank() || outboundUserApiAuth.userPassword.isNullOrBlank()){
//            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&outAuthId=${outboundUserApiAuth.providerId}" +
//                    "&errorMessage=admin.parameter.not.found")
//        }
//
//
//        try{
//            providerService.setOutBoundUserAPIParams(outboundUserApiAuth)
//        }catch (ex: Exception){
////            ex.printStackTrace()
//            logger.error(ex.message, ex)
//            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&outAuthId=${outboundUserApiAuth.providerId}" +
//                    "&errorMessage=admin.system.error")
//        }
//
//        return redirectToProvidersPage(pageNumber, null)
//    }

    private fun redirectToProvidersPage(pageNumber: Int?, linkId: Long?): ModelAndView {
        var url = "redirect:/admin/outbound-participants"

        pageNumber?.let {
            url = "$url?pageNumber=$pageNumber"

            linkId?.let {
                url = "$url&linkId=$linkId"
            }
        }

        return ModelAndView("$url&success=true")
    }

//    @GetMapping("/admin/{linkId}/unlinkParticipant/{participantId}")
//    fun unlinkParticipant(@PathVariable("linkId") linkId: Long,
//                          @PathVariable("participantId") participantId: Long,
//                          @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {
//
//        if(!checkAuth.hasAuthorization()){
//            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&accessDenied=true")
//        } else {
//            providerService.unlinkProvidersFromInboundParticipant(inboundParticipantId = participantId,
//                    providerIds = arrayOf(linkId))
//            return redirectToProvidersPage(pageNumber = pageNumber, linkId = linkId)
//        }
//    }

//    @GetMapping("/admin/{linkId}/linkParticipant/{participantId}")
//    fun linkParticipant(@PathVariable("linkId") linkId: Long,
//                        @PathVariable("participantId") participantId: Long,
//                        @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {
//
//        if(!checkAuth.hasAuthorization()){
//            return ModelAndView("redirect:/admin/outbound-participants?pageNumber=$pageNumber&accessDenied=true")
//        } else {
//            providerService.linkProviderToInboundParticipant(inboundParticipantId = participantId,
//                    providerIds = arrayOf(linkId))
//            return redirectToProvidersPage(pageNumber = pageNumber, linkId = linkId)
//        }
//
//    }
}