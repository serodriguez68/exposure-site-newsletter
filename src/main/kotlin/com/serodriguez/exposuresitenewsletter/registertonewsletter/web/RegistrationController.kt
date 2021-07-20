package com.serodriguez.exposuresitenewsletter.registertonewsletter.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
@RequestMapping("/", "/registrations")
class RegistrationController {

    @GetMapping("/new", "/")
    fun new(viewModel: Model): String {
        viewModel.addAttribute("registration", Registration())
        return "registrations/new"
    }

    @PostMapping
    fun create(@ModelAttribute registration: Registration, viewModel: Model): String {
        registration.email = "submitted"
        viewModel.addAttribute("registration", registration)
        return "registrations/new"
    }
}