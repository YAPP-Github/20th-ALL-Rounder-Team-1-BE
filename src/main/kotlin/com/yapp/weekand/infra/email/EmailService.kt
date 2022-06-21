package com.yapp.weekand.infra.email

import com.yapp.weekand.common.util.Logger
import com.yapp.weekand.domain.auth.exception.EmailFailException
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.context.Context
import org.thymeleaf.TemplateEngine
import javax.mail.internet.MimeMessage

@Service
@Transactional(readOnly = true)
class EmailService (
        private val emailSender: JavaMailSender,
        private val templateEngine: TemplateEngine
) {
    @Value("\${spring.mail.username}")
    private lateinit var WEEKAND_EMAIL: String

    fun sendEmail(email: String, content: String, title: String) {
        val mimeMessage = createMessage(email, title, content)
        try {
            emailSender.send(mimeMessage)
        } catch (e: Exception) {
            Logger.info(e.message)
            throw EmailFailException()
        }
    }

    private fun createMessage(email: String, title: String, content: String): MimeMessage {
        val mimeMessage = emailSender.createMimeMessage()
        mimeMessage.addRecipients(MimeMessage.RecipientType.TO, email)
        mimeMessage.setSubject("[Weekand] " + title)
        mimeMessage.setText(setHtml(content), "utf-8", "html")
        mimeMessage.setFrom(WEEKAND_EMAIL)
        return mimeMessage
    }

    private fun setHtml(content: String): String? {
        var context = Context()
        context.setVariable("code", content)
        return templateEngine.process("mail", context)
    }
}