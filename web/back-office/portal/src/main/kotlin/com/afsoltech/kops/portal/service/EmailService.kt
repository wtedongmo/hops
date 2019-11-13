package com.nanobnk.epayment.portal.service

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service

/**
 * @author shrisowdhaman
 * Dec 18, 2017
 */
@Service
class EmailService {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    lateinit var javaMailSender: JavaMailSender

    fun sendOtpMessage(to: String, subject: String, message: String) {

        val simpleMailMessage = SimpleMailMessage()
        simpleMailMessage.setTo(to)
        simpleMailMessage.subject = subject
        simpleMailMessage.text = message

        logger.info(subject)
        logger.info(to)
        logger.info(message)

        //Uncomment to send mail
        javaMailSender.send(simpleMailMessage)
    }
}
