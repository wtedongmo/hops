package com.nanobnk.epayment.administration.controller

import com.nanobnk.epayment.administration.utils.CheckAuth
import com.nanobnk.epayment.core.administration.controller.AbstractBasePagingController
import com.nanobnk.epayment.entity.*
import com.nanobnk.epayment.model.BaseEntityModel
import com.nanobnk.epayment.model.BaseTypeModel
import com.nanobnk.epayment.model.InboundParticipantModel
import com.nanobnk.epayment.model.OutboundParticipantModel
import com.nanobnk.epayment.repository.DeclarationTypeRepository
import com.nanobnk.epayment.repository.NoticeTypeRepository
import com.nanobnk.epayment.repository.PaymentCategoryRepository
import com.nanobnk.epayment.repository.PaymentMethodRepository
import com.nanobnk.epayment.service.BaseEntitiesService
import com.nanobnk.epayment.service.InboundParticipantService
import com.nanobnk.epayment.service.OutboundParticipantService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@RequestMapping("/admin/base-entities")
@RestController
class BaseEntitiesController(val baseEntitiesService: BaseEntitiesService): AbstractBasePagingController() {
    companion object : KLogging()

    @Autowired
    lateinit var checkAuth: CheckAuth

    private val showTypeAddFormField = "showTypeAddForm"

    @GetMapping
    fun loadBaseEntityMainPage(@RequestParam(value = "pageNumber", required = false) pageNumber: Int?,
                                        @RequestParam(value = "showTypeAddForm", required = false) showTypeAddForm: Boolean?,
                                        @RequestParam(value = "editId", required = false) editId: Long?,
        @RequestParam(value = "id", required = false) entityId: Int?, @RequestParam(value = "deleteId", required = false) deleteId: Long?,
                               @RequestParam(value = "accessDenied", required = false) accessDenied: Boolean?
    ): ModelAndView {
        val mav = ModelAndView("entities/base-entities")

        mav.addObject(showTypeAddFormField, showTypeAddForm ?: false)
        val baseEntity = BaseEntityModel()
        baseEntity.entityId = entityId?:1
        mav.addObject("baseEntity", baseEntity)

        mav.addObject("id", baseEntity.entityId)

        val hasAuth = checkAuth.hasAuthorization()
        var deniedOp: Boolean=false
        var id:Long?=null
        editId?.let {
            if(!hasAuth) deniedOp=true
            else {
                val modelEntity = baseEntitiesService.getModelEntityToEdit(entityId, editId)

                if (modelEntity == null)
                    mav.addObject("NotExistbaseEntity", "unknow.entity")
                else {
                    mav.addObject(showTypeAddFormField, true)
                    mav.addObject("baseEntity", modelEntity)
                }
            }
        }

        deleteId?.let {
            if(!hasAuth) deniedOp=true
            else {
                val result = baseEntitiesService.delete(entityId, deleteId)
                if (result.equals(-1L))
                    mav.addObject("DeleteMessage", "unable.to.delete")
                else
                    mav.addObject("DeleteMessage", "success.delete")
            }
        }

        if(deniedOp) {
            mav.addObject("accessDenied", "Access.Denied")
        }
        accessDenied?.let {
            if(accessDenied)
                mav.addObject("accessDenied", "Access.Denied")
        }

//        val baseEntities = baseEntitiesService.getModelEntitiesList(entityId)
        val baseEntities = baseEntitiesService.retrieveAllElements(entityId, calculateCurrentPage(pageNumber), DEFAULT_PAGE_SIZE)
        baseEntities.let {
            presetPagingValues(baseEntities, mav)
        }
//        mav.addObject("totalNumberElements", baseEntities.size)
        mav.addObject("baseEntities", baseEntities)

        return mav
    }

    @PostMapping
    fun saveBaseEntity(@ModelAttribute("baseEntity") baseEntity: BaseEntityModel,
                       @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        if(!checkAuth.hasAuthorization()){
            return ModelAndView("redirect:/admin/base-entities?pageNumber=$pageNumber&id=${baseEntity.entityId}&accessDenied=true")
        }
        baseEntitiesService.saveBaseEntity(baseEntity)

        return redirectToPage(pageNumber, null, baseEntity.entityId)
    }

    fun redirectToPage(pageNumber: Int?, id: Long?, entityId: Int?): ModelAndView {
        var url = "redirect:/admin/base-entities"

        pageNumber?.let {
            url = "$url?pageNumber=$pageNumber"

            id?.let {
                url = "$url&noticeTypeId=$id"
            }
        }

        entityId?.let {
            if(pageNumber==null)
                url = "$url?id=$entityId"
            else
                url = "$url&id=$entityId"
        }

        return ModelAndView(url)
    }

}