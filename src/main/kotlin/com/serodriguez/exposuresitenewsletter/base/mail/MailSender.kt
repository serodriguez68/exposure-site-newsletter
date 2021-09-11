package com.serodriguez.exposuresitenewsletter.base.mail

import org.springframework.mail.SimpleMailMessage

interface MailSender {
    fun send(simpleMessage: SimpleMailMessage)
}