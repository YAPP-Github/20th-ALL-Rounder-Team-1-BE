package com.yapp.weekand.infra.email

import com.yapp.weekand.common.util.Logger
import com.yapp.weekand.domain.auth.exception.EmailSendFailException
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
            throw EmailSendFailException()
        }
    }

	fun sendTempPasswordEmail(email: String, tempPassword: String, title: String) {
        val mimeMessage = createMessageForTempPassword(email, title, tempPassword)
        try {
            emailSender.send(mimeMessage)
        } catch (e: Exception) {
            Logger.info(e.message)
            throw EmailSendFailException()
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

	private fun createMessageForTempPassword(email: String, title: String, tempPassword: String): MimeMessage {
        val mimeMessage = emailSender.createMimeMessage()
        mimeMessage.addRecipients(MimeMessage.RecipientType.TO, email)
        mimeMessage.setSubject("[Weekand] " + title)
        mimeMessage.setText(setTempPasswordHtml(tempPassword, email), "utf-8", "html")
        mimeMessage.setFrom(WEEKAND_EMAIL)
        return mimeMessage
    }

    private fun setHtml(content: String): String? {
        var context = Context()
        context.setVariable("code", content)
        return templateEngine.process("mail", context)
    }

	private fun setTempPasswordHtml(tempPassword: String, email: String): String? {
        var context = Context()
        context.setVariable("tempPassword", tempPassword)
        context.setVariable("userEmail", email)
        return templateEngine.process("temp_password_mail", context)
    }
}
