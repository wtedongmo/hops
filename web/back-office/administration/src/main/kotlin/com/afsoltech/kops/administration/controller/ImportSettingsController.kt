package com.nanobnk.epayment.administration.controller

import com.nanobnk.epayment.administration.utils.CheckAuth
import java.io.File
import java.io.IOException
import java.util.ArrayList

import javax.validation.Valid

import com.nanobnk.epayment.administration.utils.FileBucket
import com.nanobnk.epayment.administration.utils.FileValidator
import com.nanobnk.epayment.model.attribute.UserPrivilege
import com.nanobnk.epayment.model.attribute.UserType
import com.nanobnk.epayment.service.ImportSettingsEntitiesService
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.FileCopyUtils
import org.springframework.validation.BindingResult
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.ModelAndView


@Controller
class ImportSettingsController(val importSettingsEntitiesService: ImportSettingsEntitiesService) {

    companion object : KLogging()
    @Autowired
    internal var fileValidator: FileValidator? = null

    @Value("\${save.entities.import-export.dir}")
    lateinit var UPLOAD_LOCATION: String

    @Autowired
    lateinit var checkAuth: CheckAuth

    @InitBinder("fileBucket")
    protected fun initBinderFileBucket(binder: WebDataBinder) {
        binder.validator = fileValidator
    }

    @GetMapping("/admin/user/import-settings")
    fun getFotmUploadSetting(): ModelAndView  {
        if (checkAuth.hasAuthorization()) {
            val model = ModelAndView()
            val fileModel = FileBucket()
            model.addObject("fileBucket", fileModel)
            model.viewName = "user/import-settings"
            return model
        } else{
            return ModelAndView("redirect:/admin/users?accessDenied=true")
        }
    }

    @PostMapping("/admin/user/import-settings")
    @Throws(IOException::class)
    fun uploadSettingCsvZip(@ModelAttribute("fileBucket") fileBucket: FileBucket): ModelAndView {
        //result: BindingResult

//        if (result.hasErrors()) {
//            logger.trace("validation errors")
//            val ermod = ModelAndView()
//            ermod.addObject("error", "validation errors: "+result.getGlobalError().defaultMessage)
//            ermod.viewName = "user/import-settings"
//            return ermod
//        } else {
        if (checkAuth.hasAuthorization()) {
            logger.trace("Fetching file")
            val multipartFile = fileBucket.file

            //Now do something with file...
            FileCopyUtils.copy(fileBucket.file!!.bytes, File(UPLOAD_LOCATION + fileBucket.file!!.originalFilename))

            importSettingsEntitiesService.importSettingsZipCsvFiles(File(UPLOAD_LOCATION + fileBucket.file!!.originalFilename))

            val fileName = multipartFile!!.originalFilename
            val model = ModelAndView("redirect:/admin/users?pageNumber=1")
            model.addObject("successImport", "Success import file settings : $fileName")
            return model
//        }
        } else{
            return ModelAndView("redirect:/admin/users?accessDenied=true")
        }
    }

}