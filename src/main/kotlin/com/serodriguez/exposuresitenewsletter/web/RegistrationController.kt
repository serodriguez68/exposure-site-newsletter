package com.serodriguez.exposuresitenewsletter.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.ui.Model

@Controller
@RequestMapping("/", "/registrations")
class RegistrationController {

    @GetMapping("/new", "/")
    fun newRegistration(viewModel: Model): String {
        return "registrations/new"
    }
}