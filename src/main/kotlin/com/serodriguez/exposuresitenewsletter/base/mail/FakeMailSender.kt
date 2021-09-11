package com.serodriguez.exposuresitenewsletter.base.mail

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Profile
import org.springframework.mail.SimpleMailMessage
import org.springframework.stereotype.Component

@Component
@Profile("test")
class FakeMailSender(
    @Autowired val fakeMailServer: FakeMailServer
): MailSender {
    override fun send(simpleMessage: SimpleMailMessage) {
        fakeMailServer.send(simpleMessage)
    }
}

@Component
class FakeMailServer(
    private var messages: MutableList<SimpleMailMessage> = mutableListOf()
) {
    fun send(simpleMessage: SimpleMailMessage) {
        messages.add(simpleMessage)
    }

    fun messages() = messages.toList()

    fun reset() {
        messages = mutableListOf()
    }
}