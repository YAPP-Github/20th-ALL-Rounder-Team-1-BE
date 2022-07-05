package com.yapp.weekand.infra.email

import com.yapp.weekand.common.util.Logger
import com.yapp.weekand.domain.auth.exception.EmailSendFailException
import com.yapp.weekand.infra.email.exception.EmailReplacementInvalidException
import com.yapp.weekand.infra.email.replacement.EmailReplacement
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context
import javax.mail.internet.MimeMessage

@Service
@Transactional(readOnly = true)
class EmailService(
	private val emailSender: JavaMailSender,
	private val templateEngine: TemplateEngine
) {
	@Value("\${spring.mail.username}")
	private lateinit var WEEKAND_EMAIL: String

	fun sendEmail(receiver: String, replacements: EmailReplacement) {
		if (!replacements.validate()) {
			throw EmailReplacementInvalidException()
		}

		val content = getReplacedHtml(replacements)
		val title = replacements.title
		val mimeMessage = createMessage(receiver, title, content)

		try {
			emailSender.send(mimeMessage)
		} catch (e: Exception) {
			Logger.info(e.message)
			throw EmailSendFailException()
		}
	}

	private fun createMessage(receiverEmail: String, title: String, content: String): MimeMessage {
		val mimeMessage = emailSender.createMimeMessage()
		mimeMessage.addRecipients(MimeMessage.RecipientType.TO, receiverEmail)
		mimeMessage.subject = "[Weekand] $title"
		mimeMessage.setText(content, "utf-8", "html")
		mimeMessage.setFrom(WEEKAND_EMAIL)
		return mimeMessage
	}

	private fun getReplacedHtml(replacements: EmailReplacement): String {
		val context = Context()
		replacements.replacements.forEach { (key, replacement) -> context.setVariable(key, replacement) }
		return templateEngine.process(replacements.templateName, context)
	}
}
