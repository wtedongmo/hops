package com.nanobnk.epayment.portal.utils

import java.io.File
import java.io.IOException
import java.nio.file.Files

/**
 * @author shrisowdhaman
 * Dec 18, 2017
 */
class EmailTemplate(private val templateId: String) {

    private var template: String? = null

    private val replacementParams: Map<String, String>? = null

    init {
        try {
            this.template = loadTemplate(templateId)
        } catch (e: Exception) {
            this.template = "Your Otp Number is "
        }

    }

    @Throws(Exception::class)
    private fun loadTemplate(templateId: String): String {
        val classLoader = javaClass.classLoader
        val file = File(classLoader.getResource(templateId)!!.file)
        var content = "Empty"
        try {
            content = String(Files.readAllBytes(file.toPath()))
        } catch (e: IOException) {
            throw Exception("Could not read template with ID = $templateId")
        }

        return content
    }

    fun getTemplate(replacements: Map<String, String>): String {
        var cTemplate = this.template

        //Replace the String
        for ((key, value) in replacements) {
            cTemplate = cTemplate!!.replace("{{$key}}", value)
        }
        return cTemplate!!
    }
}
