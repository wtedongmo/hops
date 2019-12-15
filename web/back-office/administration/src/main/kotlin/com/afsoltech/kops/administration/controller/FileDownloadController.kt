package com.afsoltech.kops.administration.controller

import java.io.BufferedInputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.URLConnection
import java.nio.charset.Charset

import javax.servlet.http.HttpServletResponse

import org.springframework.stereotype.Controller
import org.springframework.ui.ModelMap
import org.springframework.util.FileCopyUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@Controller
class FileDownloadController {


    @RequestMapping("/download", method = [RequestMethod.GET])
    fun getHomePage(model: ModelMap): String {
        return "download"
    }

    /*
     * Download a file from
     *   - inside project, located in resources folder.
     *   - outside project, located in File system somewhere.
     */
    @GetMapping("/downloadFile/{type}")
    @Throws(IOException::class)
    fun downloadFile(response: HttpServletResponse, @PathVariable("type") type: String) {

        var file: File? = null

        if (type.equals("internal", ignoreCase = true)) {
            val classloader = Thread.currentThread().contextClassLoader
            file = File(classloader.getResource(INTERNAL_FILE)!!.file)
        } else {
            file = File(EXTERNAL_FILE_PATH)
        }

        if (!file.exists()) {
            val errorMessage = "Sorry. The file you are looking for does not exist"
            println(errorMessage)
            val outputStream = response.outputStream
            outputStream.write(errorMessage.toByteArray(Charset.forName("UTF-8")))
            outputStream.close()
            return
        }

        var mimeType: String? = URLConnection.guessContentTypeFromName(file.name)
        if (mimeType == null) {
            println("mimetype is not detectable, will take default")
            mimeType = "application/octet-stream"
        }

        println("mimetype : $mimeType")

        response.contentType = mimeType

        /* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
        response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.name + "\""))


        /* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
        //response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

        response.setContentLength(file.length().toInt())

        val inputStream = BufferedInputStream(FileInputStream(file))

        //Copy bytes from source to destination(outputstream in this example), closes both streams.
        FileCopyUtils.copy(inputStream, response.outputStream)
    }

    companion object {

        private val INTERNAL_FILE = "attestationMS.doc"
        private val EXTERNAL_FILE_PATH = "D:/Wilfried/badge.pdf"
    }

}