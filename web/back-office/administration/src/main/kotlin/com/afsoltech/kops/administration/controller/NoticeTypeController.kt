package com.nanobnk.epayment.administration.controller

import com.nanobnk.epayment.administration.utils.CheckAuth
import com.nanobnk.epayment.core.administration.controller.AbstractBasePagingController
import com.nanobnk.epayment.entity.BaseEntity
import com.nanobnk.epayment.entity.NoticeTypeEntity
import com.nanobnk.epayment.model.BaseTypeModel
import com.nanobnk.epayment.model.InboundParticipantModel
import com.nanobnk.epayment.model.OutboundParticipantModel
import com.nanobnk.epayment.repository.NoticeTypeRepository
import com.nanobnk.epayment.service.BaseEntitiesService
import com.nanobnk.epayment.service.InboundParticipantService
import com.nanobnk.epayment.service.OutboundParticipantService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@RequestMapping("/admin/customs-types")
@RestController
class NoticeTypeController(val noticeTypeRepository: NoticeTypeRepository, val baseEntitiesService: BaseEntitiesService) : AbstractBasePagingController() {
    companion object : KLogging()

    private val showTypeAddFormField = "showTypeAddForm"
    @Autowired
    lateinit var checkAuth: CheckAuth

    @GetMapping
    fun loadNoticeTypesMainPage(@RequestParam(value = "pageNumber", required = false) pageNumber: Int?,
                                        @RequestParam(value = "showTypeAddForm", required = false) showTypeAddForm: Boolean?,
                                        @RequestParam(value = "editId", required = false) editId: Long?,
                                @RequestParam(value = "deleteId", required = false) deleteId: Long?,
                                @RequestParam(value = "errorMessage", required = false) errorMessage: String?,
                                @RequestParam(value = "accessDenied", required = false) accessDenied: Boolean?
    ): ModelAndView {
        val mav = ModelAndView("entities/customs-types")

        mav.addObject(showTypeAddFormField, showTypeAddForm ?: false)
        mav.addObject("noticeType", BaseTypeModel())

        val hasAuth = checkAuth.hasAuthorization()
        var deniedOp: Boolean=false
        editId?.let {
            if(!hasAuth) deniedOp=true
            else {
                val noticeType = noticeTypeRepository.findOne(editId)
                mav.addObject(showTypeAddFormField, true)
                mav.addObject("noticeType", BaseTypeModel(noticeType.id, noticeType.code, noticeType.name, noticeType.status))
            }
        }

        deleteId?.let {
            if(!hasAuth) deniedOp=true
            else {
                val result = baseEntitiesService.delete(2, deleteId)
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
        errorMessage?.let {
            mav.addObject("errorMessage", errorMessage)
        }
        val noticeTypes = noticeTypeRepository.findAll()
        mav.addObject("totalNumberElements", noticeTypes.size)
        val baseTypes = noticeTypes.map { it ->
            BaseTypeModel(it.id, it.code, it.name, it.status)
        }
        mav.addObject("noticeTypes", baseTypes)

        return mav
    }

    @PostMapping
    fun saveNoticeType(@ModelAttribute("noticeType") baseTypeModel: BaseTypeModel,
                       @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView {

        if(!checkAuth.hasAuthorization()){
            baseTypeModel.id?.let {
                return ModelAndView("redirect:/admin/customs-types?pageNumber=$pageNumber&editId=${baseTypeModel.id}" +
                        "&showTypeAddForm=true&errorMessage=admin.user.not.authorized")
            }
            return ModelAndView("redirect:/admin/customs-types?pageNumber=$pageNumber&showTypeAddForm=true" +
                    "&errorMessage=admin.user.not.authorized")
        }
        val entity  = NoticeTypeEntity(baseTypeModel.id, baseTypeModel.code, baseTypeModel.name, baseTypeModel.status)
//        val noticeType = entity as NoticeTypeEntity
//        noticeType.id =
        try {
            noticeTypeRepository.save(entity)
        }catch (ex: Exception){
//            ex.printStackTrace()
            logger.error(ex.message, ex)
            return ModelAndView("redirect:/admin/customs-types?pageNumber=$pageNumber&showTypeAddForm=true&errorMessage=admin.system.error")
        }

        return redirectToNoticeTypesPage(pageNumber, null, null)
    }

    fun redirectToNoticeTypesPage(pageNumber: Int?, linkId: Long?, noticeTypeId: Long?): ModelAndView {
        var url = "redirect:/admin/customs-types"

        pageNumber?.let {
            url = "$url?pageNumber=$pageNumber"

            noticeTypeId?.let {
                url = "$url&noticeTypeId=$noticeTypeId"
            }
        }

        return ModelAndView(url)
    }

//    private fun deleteNoticeType(deleteId: Long?): Long{
//        val ent =  noticeTypeRepository.findOne(deleteId)
//        if(ent.id!=null && ent.id==deleteId){
//            val notices = noticeRepository.findByNoticeType(ent.code!!)
//            if(notices.size==0){
//                noticeTypeRepository.delete(ent)
//                deleteId
//            }else
//                -1L
//        }else
//            -1L
//        return result
//    }
}