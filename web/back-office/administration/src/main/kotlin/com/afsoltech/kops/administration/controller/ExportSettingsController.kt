package com.afsoltech.kops.administration.controller

import com.afsoltech.core.service.UserService
import com.afsoltech.core.service.security.setting.ExportSettingsEntitiesService
import com.afsoltech.kops.administration.utils.CheckAuth
import mu.KLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.util.FileCopyUtils
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.ModelAndView
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.net.URLConnection
import javax.servlet.http.HttpServletResponse

//@RequestMapping(value = ["/users", "/"])
@RestController
class ExportSettingsController(val userService: UserService, val exportSettingsEntitiesService: ExportSettingsEntitiesService
                               )  {

    companion object : KLogging()
    @Autowired
    lateinit var checkAuth: CheckAuth

    @GetMapping("/admin/export-settings")
    fun exportSettingsCsvZip(response: HttpServletResponse) {

        if(checkAuth.hasAuthorization()) {

            //, @RequestParam(value = "pageNumber", required = false) pageNumber: Int?): ModelAndView
            val fileSettings = exportSettingsEntitiesService.exportSettingsCsvZip()

            var mimeType: String? = URLConnection.guessContentTypeFromName(fileSettings.name)
            if (mimeType == null) {
                println("mimetype is not detectable, will take default")
                mimeType = "application/octet-stream"
            }

            logger.trace { "mimetype : $mimeType" }

            response.contentType = mimeType

            /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + fileSettings.name + "\""))


            /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
            //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
            response.setContentLength(fileSettings.length().toInt())

            val inputStream = BufferedInputStream(FileInputStream(fileSettings))

            //Copy bytes from source to destination(outputstream in this example), closes both streams.
            FileCopyUtils.copy(inputStream, response.outputStream)

            //return ModelAndView("redirect:/users?pageNumber=$pageNumber")
        }
    }

}