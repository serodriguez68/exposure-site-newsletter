package com.serodriguez.exposuresitenewsletter.base.mail

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
@Profile("!test")
class RealMailSender(
    @Autowired private val mailSender: JavaMailSender,
): MailSender {
    override fun send(simpleMessage: SimpleMailMessage) {
        mailSender.send(simpleMessage)
    }
}