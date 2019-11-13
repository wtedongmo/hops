package com.nanobnk.epayment.reporting.controller

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import java.io.File

@RequestMapping("/csv")
@Controller
class CSVFileController {

    @GetMapping(produces = [(MediaType.APPLICATION_OCTET_STREAM_VALUE)])
    fun loadTransactionsListAsCSV(@RequestParam filename: String): ResponseEntity<Resource> {

        var csvFile = File("files/$filename")

        if (!csvFile.exists()) {
            csvFile.createNewFile()
        }

        val resource = UrlResource(csvFile.toURI())
        val contentType = "text/csv"

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource)
    }

}