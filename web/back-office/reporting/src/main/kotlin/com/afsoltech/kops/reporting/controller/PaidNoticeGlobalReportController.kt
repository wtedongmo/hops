package com.nanobnk.epayment.reporting.controller

import com.nanobnk.epayment.model.attribute.BaseStatus
import com.nanobnk.epayment.model.inbound.NoticeReportResponseDto
import com.nanobnk.epayment.reporting.service.global.PaidNoticeListGlobalReportService
import com.nanobnk.epayment.reporting.utils.PaidNoticeReportModel
import com.nanobnk.epayment.repository.*
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView

@RestController
@RequestMapping("/report/list-paid-customs")
class PaidNoticeGlobalReportController (val paidNoticeListReportService: PaidNoticeListGlobalReportService) {

    companion object : KLogging()

    @Autowired
    lateinit var noticeTypeRepository: NoticeTypeRepository

    @Autowired
    lateinit var participantViewRepository: ParticipantViewRepository

    @Autowired
    lateinit var paymentCategoryRepository: PaymentCategoryRepository

    @Autowired
    lateinit var paymentMethodRepository: PaymentMethodRepository

    @Autowired
    lateinit var declarationTypeRepository: DeclarationTypeRepository

    @Autowired
    lateinit var beneficiaryRepository: BeneficiaryRepository

    @Autowired
    lateinit var issuerOfficeRepository: IssuerOfficeRepository


    @GetMapping
    fun paidNoticeReportList(@RequestParam("id", required = false) id: Int?, model: Model) :ModelAndView{

        val mav = ModelAndView("report/paid-customs-form")
        val reportModel = PaidNoticeReportModel()
        mav.addObject("PaidNoticeModel", reportModel)

        val offices = issuerOfficeRepository.findByStatus(BaseStatus.ACTIVE).map { it -> it.code }
        mav.addObject("offices", offices)
        val listNoticeType = noticeTypeRepository.findAll()
        mav.addObject("noticeTypes", listNoticeType)

        val listParticipant = participantViewRepository.findAll()
        mav.addObject("participants", listParticipant)

        val listBenef = beneficiaryRepository.findByStatus(BaseStatus.ACTIVE)
        mav.addObject("beneficiaries", listBenef)

        val listPayCat = paymentCategoryRepository.findByStatus(BaseStatus.ACTIVE)
        mav.addObject("paymentCats", listPayCat)

        val listDecType = declarationTypeRepository.findByStatus(BaseStatus.ACTIVE)
        mav.addObject("decTypes", listDecType)

        val listPayMeth = paymentMethodRepository.findByStatus(BaseStatus.ACTIVE)
        mav.addObject("paymentMeths", listPayMeth)

        return mav
    }

    @PostMapping
    fun generateReport(@ModelAttribute("PaidNoticeModel") reportModel: PaidNoticeReportModel) : ModelAndView {



        val result = paidNoticeListReportService.genericPaidNoticeListReport(reportModel)

        val modelAndView = ModelAndView()
//        modelAndView.addObject("username", auth.name)
        val listPaidNotice = result.get("ModelResponse")
        logger.trace { "\n$listPaidNotice" }
        modelAndView.addObject("PaidNoticeList", listPaidNotice?: emptyList<NoticeReportResponseDto>())
        modelAndView.addObject("filenamePdf", result.get("PDFFile"))
        modelAndView.addObject("filenameCsv", result.get("CsvFile"))
        modelAndView.addObject("parentMenuHighlight", "notices-index")
        modelAndView.addObject("menuHighlight", "notices-paid")
        modelAndView.viewName = "report/list-paid-customs"
        return modelAndView
    }

}