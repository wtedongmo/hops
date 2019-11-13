package com.nanobnk.epayment.administration.controller

import com.nanobnk.epayment.service.mapper.ModelEntityMapping
import com.nanobnk.epayment.core.administration.controller.AbstractBasePagingController
import com.nanobnk.epayment.model.EtlReportModel
import com.nanobnk.epayment.repository.ETLReportRepository
import com.nanobnk.epayment.service.BaseEntitiesService
import com.nanobnk.epayment.service.EltReportService
import mu.KLogging
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@RequestMapping("/admin/load-file-reports")
@RestController
class EtlReportController(val etlReportRepository: ETLReportRepository, val etlReportService: EltReportService) : AbstractBasePagingController() {
    companion object : KLogging()

    private val showTypeAddFormField = "showTypeAddForm"

    @GetMapping
    fun loadEltReportMainPage(@RequestParam(value = "pageNumber", required = false) pageNumber: Int?,
                                        @RequestParam(value = "showTypeAddForm", required = false) showTypeAddForm: Boolean?,
                                        @RequestParam(value = "viewId", required = false) viewId: Long?
    ): ModelAndView {
        val mav = ModelAndView("entities/load-file-reports")

        mav.addObject(showTypeAddFormField, showTypeAddForm ?: false)
        mav.addObject("etlReport", EtlReportModel())

        viewId?.let {
            val etlReport = etlReportRepository.findOne(viewId)
            mav.addObject(showTypeAddFormField, true)
            mav.addObject("etlReport", ModelEntityMapping.EtlReportEntityToModel.from(etlReport))
        }

//        deleteId?.let {
//            val result = etlReportRepository.delete( deleteId)
////            if(result.equals(-1L))
////                mav.addObject("DeleteMessage", "unable.to.delete")
////            else
//                mav.addObject("DeleteMessage", "success.delete")
//        }


        val etlReports = etlReportService.retrieveAllEtlReport(calculateCurrentPage(pageNumber), DEFAULT_PAGE_SIZE)
//        mav.addObject("totalNumberElements", etlReports.totalElements)
        etlReports.let {
            presetPagingValues(etlReports, mav)
        }
//        val baseTypes = etlReports.map { it ->
//            ModelEntityMapping.EtlReportEntityToModel.from(it)
//        }
//        mav.addObject("etlReports", baseTypes)
        mav.addObject("etlReports", etlReports)

        return mav
    }

}