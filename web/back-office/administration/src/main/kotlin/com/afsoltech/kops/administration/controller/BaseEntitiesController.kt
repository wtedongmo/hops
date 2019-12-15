package com.afsoltech.kops.administration.controller

import com.afsoltech.core.backoffice.controller.AbstractBasePagingController
import com.afsoltech.core.model.BaseEntityModel
import com.afsoltech.core.model.SearchModel
import com.afsoltech.kops.administration.utils.CheckAuth
import com.afsoltech.kops.service.backoffice.BaseEntitiesService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import javax.servlet.http.HttpServletRequest

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
                               @RequestParam(value = "accessDenied", required = false) accessDenied: Boolean?,
                               @RequestParam(value = "success", required = false) success: Boolean?,
                               @ModelAttribute("searchForm") searchForm: SearchModel?,
                               request: HttpServletRequest
    ): ModelAndView {
        val mav = ModelAndView("entities/base-entities")

        mav.addObject(showTypeAddFormField, showTypeAddForm ?: false)
        val baseEntity = BaseEntityModel()
        baseEntity.entityId = entityId?: searchForm?.entityId?: 1
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

        success?.let {
            if(success)
                mav.addObject("SuccessMessage", "operation.success")
        }

        val auth = SecurityContextHolder.getContext().authentication
        var baseEntities : Page<BaseEntityModel>? = null
        val entId = entityId?: searchForm?.entityId?:1

        if(editId!=null || deleteId!=null ){
            val baseEntSession = request.session.getAttribute(auth.name+"_baseEnt$entId")
            if(baseEntSession!= null)
                baseEntities = baseEntSession as Page<BaseEntityModel>
        }

        if(baseEntities==null && searchForm != null && !searchForm.column.isNullOrEmpty() && !searchForm.value.isNullOrEmpty()
                && !searchForm.column.equals("NONE", true) ){
            try {
                baseEntities = baseEntitiesService.search(PageRequest.of(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, Sort.Direction.ASC, "id"),
                        searchForm.column!!, searchForm.value!!, entId)
                request.session.setAttribute(auth.name+"_baseEnt$entId", baseEntities)
            }catch(ex: Exception){
                ProviderController.logger.error { ex.message +"\n"+ex.printStackTrace() }
                mav.addObject("errorMessage", "app.search.error")
                baseEntities = null
            }
        }else if(baseEntities==null)
            baseEntities = baseEntitiesService.retrieveAllElements(entId, calculateCurrentPage(pageNumber), DEFAULT_PAGE_SIZE)

        mav.addObject("searchMap", getSearchFieldMap("app.search.BaseEntity", "app.search.BaseEntity"))
        mav.addObject("searchForm", SearchModel(entityId = entId))
//        val baseEntities = baseEntitiesService.getModelEntitiesList(entityId)
        baseEntities?.let {
            presetPagingValues(baseEntities, mav)
        }
//        mav.addObject("totalNumberElements", baseEntities.size)
        mav.addObject("baseEntities", baseEntities?: ArrayList<BaseEntityModel>())

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

        return ModelAndView("$url&success=true")
    }

}