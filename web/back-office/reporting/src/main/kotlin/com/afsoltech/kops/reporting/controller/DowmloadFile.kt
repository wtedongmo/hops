package com.nanobnk.epayment.reporting.controller

import mu.KLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.net.URLConnection
import javax.servlet.http.HttpServletResponse


@Controller
@RequestMapping("/report/download")
class DowmloadFile {

    companion object : KLogging()

    @Value("\${reporting.save.report.dir}")
    lateinit var reportDir: String

    @GetMapping
    fun downloadFile(@RequestParam(value = "filename", required = false) filename: String?, response: HttpServletResponse ) {

        if(!filename.isNullOrBlank()) {
            val filePath = reportDir + filename
            val file = File(filePath)


            var mimeType: String? = URLConnection.guessContentTypeFromName(file.name)
            if (mimeType == null) {
                logger.trace("mimetype is not detectable, will take default")
                mimeType = "application/octet-stream"
            }

            logger.trace { "mimetype : $mimeType" }

            response.contentType = mimeType

            /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
                while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
//            response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.name + "\""))
            response.setHeader("Content-Disposition", String.format("attachment; filename=" + file.name ))


            /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
            //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));
            //val file = File(filename)
            response.setContentLength(file.length().toInt())

            val inputStream = BufferedInputStream(FileInputStream(file))

            //Copy bytes from source to destination(outputstream in this example), closes both streams.
            FileCopyUtils.copy(inputStream, response.outputStream)
            response.outputStream.flush()
        }

    }
}