package com.nanobnk.epayment.reporting.controller

import com.nanobnk.epayment.model.attribute.BaseStatus
import com.nanobnk.epayment.model.attribute.UserType
import com.nanobnk.epayment.reporting.service.DelayPaidNoticeListReportService
import com.nanobnk.epayment.reporting.service.PaidNoticeListReportService
import com.nanobnk.epayment.reporting.utils.CheckAuth
import com.nanobnk.epayment.reporting.utils.PaidNoticeAppName
import com.nanobnk.epayment.reporting.utils.PaidNoticeReportModel
import com.nanobnk.epayment.repository.*
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.ui.Model
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.lang.Exception
import java.net.URLConnection
import javax.servlet.http.HttpServletResponse
import javax.validation.Valid

@RestController
@RequestMapping("/report/paid-customs")
class PaidNoticeReportController (val paidNoticeListReportService: PaidNoticeListReportService, val userRepository: UserRepository,
                                  val checkAuth: CheckAuth) {

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
    fun paidNoticeReportList(@RequestParam("id") id: Int?, @RequestParam(value = "pdf", required = false) pdf: String?,
                             @RequestParam(value = "csv", required = false) csv: String?,
                             @RequestParam(value = "error", required = false) error: String?) :ModelAndView{

        val mav = ModelAndView()
        val reportModel = PaidNoticeReportModel()
        id?.let {
            reportModel.reportCode = id
        }

        error?.let {
            mav.addObject("errorMessage", error)
        }
        val offices = issuerOfficeRepository.findByStatus(BaseStatus.ACTIVE).map { it -> it.code }
        mav.addObject("offices", offices)

        val auth = SecurityContextHolder.getContext().authentication

        if(checkAuth.hasNanoAuth(auth)) {
            if (id == null || id == 101 || (id <= 100 && id > 116)) {
                mav.addObject("paidnotice", "paidnotice")
                mav.viewName = "report/paid-customs-main-page-report"
                mav.addObject("otherUser", "otherUser")
            } else if (id == 102) {
                mav.addObject("paidnoticeOnTime", "paidnoticeOnTime")
                mav.viewName = "report/paid-customs-main-page-report"
                mav.addObject("otherUser", "otherUser")
            } else if (id == 103) {
                mav.addObject("paidnoticeDelay", "paidnoticeDelay")
                mav.viewName = "report/paid-customs-main-page-report"
                mav.addObject("otherUser", "otherUser")
            } else if (id == 104) {
//            val listNoticeType = noticeTypeRepository.findAll()
                val listNoticeType = noticeTypeRepository.findByStatus(BaseStatus.ACTIVE)
                mav.addObject("noticeTypes", listNoticeType)
                mav.viewName = "report/paid-customs-type-report"
            } else if (id == 105) {
                val listParticipant = participantViewRepository.findAll()
                mav.addObject("participants", listParticipant)
                mav.viewName = "report/paid-customs-participant-report"
            } else if (id == 106) {
                val listBenef = beneficiaryRepository.findByStatus(BaseStatus.ACTIVE)
                mav.addObject("beneficiaries", listBenef)
                mav.viewName = "report/paid-customs-benef-report"
            } else if (id == 107) {
                mav.viewName = "report/paid-customs-taxpayer-report"
                mav.addObject("taxpayer", "taxpayer")
            } else if (id == 108) {
                mav.viewName = "report/paid-customs-taxpayer-report"
                mav.addObject("taxpayerOntime", "taxpayerOntime")
            } else if (id == 109) {
                mav.viewName = "report/paid-customs-taxpayer-report"
                mav.addObject("taxpayerLate", "taxpayerLate")
            } else if (id == 110) {
                mav.addObject("otherUser", "otherUser")
                mav.addObject("paidnoticeOffice", "paidnoticeOffice")
                mav.viewName = "report/paid-customs-main-page-report"
            } else if (id == 111) {
                val listPayCat = paymentCategoryRepository.findByStatus(BaseStatus.ACTIVE)
                mav.addObject("paymentCats", listPayCat)
                mav.viewName = "report/paid-customs-paycat-report"
            } else if (id == 112) {
                val listDecType = declarationTypeRepository.findByStatus(BaseStatus.ACTIVE)
                mav.addObject("decTypes", listDecType)
                mav.viewName = "report/paid-customs-dectype-report"
            } else if (id == 113) {
                val listPayMeth = paymentMethodRepository.findByStatus(BaseStatus.ACTIVE)
                mav.addObject("paymentMeths", listPayMeth)
                mav.viewName = "report/paid-customs-paymeth-report"
            } else if (id == 114) {
                mav.viewName = "report/paid-customs-cda-report"
            } else if (id == 115) {
                mav.addObject("participant", "participant")
                mav.viewName = "report/paid-customs-main-page-report"
//            mav.viewName = "report/participant-reconciliation-report"
            } else if(id==116){
                val listParticipant = participantViewRepository.findAll()
                mav.addObject("participants", listParticipant)
                mav.viewName = "report/payment-audit-report"
            }
        }else if(checkAuth.hasParticipantAuth(auth)){
            mav.addObject("participant", "participant")
            mav.viewName = "report/paid-customs-main-page-report"
            reportModel.reportCode = 115
        }else if(checkAuth.hasProviderAuth(auth)){
            mav.addObject("provider", "provider")
            mav.viewName = "report/paid-customs-main-page-report"
            reportModel.reportCode = 117
        }


        pdf?.let {
            mav.addObject("filenamePdf", it)
        }
        csv?.let {
            mav.addObject("filenameCsv", it)
        }
        mav.addObject("PaidNoticeModel", reportModel)

        return mav
    }

//    @PostMapping//("/report/paid-customs")
//    fun paidNoticeReportList(@ModelAttribute("PaidNoticeModel") paidNoticeModel: PaidNoticeReportModel, response: HttpServletResponse, model: Model){
//
//        val params = hashMapOf<String, Any?>()
//
//        val filePath = paidNoticeListReportService.genericPaidNoticeListReport(paidNoticeModel)
//        val file = File(filePath)
//
//        var mimeType: String? = URLConnection.guessContentTypeFromName(file.name)
//        if (mimeType == null) {
//            logger.trace("mimetype is not detectable, will take default")
//            mimeType = "application/octet-stream"
//        }
//
//        logger.trace { "mimetype : $mimeType" }
//
//        response.contentType = mimeType
//
//        /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
//            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
//        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.name + "\""))
//
//
//        /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
//        //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
//        //val file = File(filename)
//        response.setContentLength(file.length().toInt())
//
//        val inputStream = BufferedInputStream(FileInputStream(file))
//
//        //Copy bytes from source to destination(outputstream in this example), closes both streams.
//        FileCopyUtils.copy(inputStream, response.outputStream)
//    }

    @PostMapping//("/report/paid-customs")
    fun paidNoticeReportList(@Valid @ModelAttribute("PaidNoticeModel") paidNoticeModel: PaidNoticeReportModel) : ModelAndView {

        val auth = SecurityContextHolder.getContext().authentication
        val username = auth.principal.toString()
        val user = userRepository.findByUsername(username)
        if(user!=null && user.type!!.equals(UserType.PARTICIPANT)){
            paidNoticeModel.reportCode=115
        }

        if(paidNoticeModel.startDate.isNullOrBlank() || paidNoticeModel.endDate.isNullOrBlank() ){
            return ModelAndView("redirect:report/paid-customs?id=${paidNoticeModel.reportCode}&error=report.params.error")
        }
//        val params = hashMapOf<String, Any?>()

        val filesList = paidNoticeListReportService.genericPaidNoticeListReport(paidNoticeModel)

        return ModelAndView("redirect:/report/paid-customs?id=${paidNoticeModel.reportCode}&pdf=${filesList.get(0)}&csv=${filesList.get(1)}")


//        filesList.forEach { filename ->
//            try {
//                downloadFile(filename, response)
//                Thread.sleep(2000)
//            }catch (ex: Exception){ ex.printStackTrace()}
//        }

    }


        fun downloadFile(filename: String, response: HttpServletResponse ) {

            val filePath = filename
            val file = File(filePath)
            var mimeType: String? = URLConnection.guessContentTypeFromName(file.name)
            if (mimeType == null) {
                logger.trace("mimetype is not detectable, will take default")
                mimeType = "application/octet-stream"
            }

            logger.trace { "mimetype : $mimeType" }
            response.contentType = mimeType
            response.setHeader("Content-Disposition", String.format("attachment; filename=" + file.name ))
            response.setContentLength(file.length().toInt())
            val inputStream = BufferedInputStream(FileInputStream(file))

            //Copy bytes from source to destination(outputstream in this example), closes both streams.
            FileCopyUtils.copy(inputStream, response.outputStream)
            response.outputStream.flush()
        }

}