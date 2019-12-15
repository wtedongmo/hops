//package com.afsoltech.kops.administration.controller
//
//import com.nanobnk.epayment.service.mapper.ModelEntityMapping
//import com.nanobnk.epayment.core.administration.controller.AbstractBasePagingController
//import com.nanobnk.epayment.model.BaseEntityModel
//import com.nanobnk.epayment.model.EtlReportModel
//import com.nanobnk.epayment.model.SearchModel
//import com.nanobnk.epayment.repository.ETLReportRepository
//import com.nanobnk.epayment.service.BaseEntitiesService
//import com.nanobnk.epayment.service.EltReportService
//import mu.KLogging
//import org.springframework.data.domain.Page
//import org.springframework.data.domain.PageRequest
//import org.springframework.data.domain.Sort
//import org.springframework.security.core.context.SecurityContextHolder
//import org.springframework.web.bind.annotation.*
//import org.springframework.web.servlet.ModelAndView
//import javax.servlet.http.HttpServletRequest
//
//@RequestMapping("/admin/load-file-reports")
//@RestController
//class EtlReportController(val etlReportRepository: ETLReportRepository, val etlReportService: EltReportService) : AbstractBasePagingController() {
//    companion object : KLogging()
//
//    private val showTypeAddFormField = "showTypeAddForm"
//
//    @GetMapping
//    fun loadEltReportMainPage(@RequestParam(value = "pageNumber", required = false) pageNumber: Int?,
//                                        @RequestParam(value = "showTypeAddForm", required = false) showTypeAddForm: Boolean?,
//                                        @RequestParam(value = "viewId", required = false) viewId: Long?,
//                              @ModelAttribute("searchForm") searchForm: SearchModel?,
//                              request: HttpServletRequest
//    ): ModelAndView {
//        val mav = ModelAndView("entities/load-file-reports")
//
//        mav.addObject(showTypeAddFormField, showTypeAddForm ?: false)
//        mav.addObject("etlReport", EtlReportModel())
//
//        viewId?.let {
//            val etlReport = etlReportRepository.findOne(viewId)
//            mav.addObject(showTypeAddFormField, true)
//            mav.addObject("etlReport", ModelEntityMapping.EtlReportEntityToModel.from(etlReport))
//        }
//
//        val auth = SecurityContextHolder.getContext().authentication
//        var etlReports : Page<EtlReportModel>? = null
//
//
//        if(etlReports==null && searchForm != null && !searchForm.column.isNullOrEmpty() && !searchForm.value.isNullOrEmpty()
//                && !searchForm.column.equals("NONE", true) ){
//            try {
//                etlReports = etlReportService.search(PageRequest(DEFAULT_PAGE_NUMBER, DEFAULT_PAGE_SIZE, Sort.Direction.ASC, "id"),
//                        searchForm.column!!, searchForm.value!!)
//                request.session.setAttribute(auth.name+"_etlReport", etlReports)
//            }catch(ex: Exception){
//                ProviderController.logger.error { ex.message +"\n"+ex.printStackTrace() }
//                mav.addObject("errorMessage", "app.search.error")
//                etlReports = null
//            }
//        }else if(etlReports==null)
//            etlReports = etlReportService.retrieveAllEtlReport(calculateCurrentPage(pageNumber), DEFAULT_PAGE_SIZE)
//
//        mav.addObject("searchMap", getSearchFieldMap("app.search.EtlReport", "app.search.EtlReport"))
//        mav.addObject("searchForm", SearchModel())
//
////        val etlReports = etlReportService.retrieveAllEtlReport(calculateCurrentPage(pageNumber), DEFAULT_PAGE_SIZE)
//        etlReports?.let {
//            presetPagingValues(etlReports, mav)
//        }
//        mav.addObject("etlReports", etlReports)
//
//        return mav
//    }
//
//}