package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.web

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping

@Controller
@RequestMapping("/", "/subscriptions")
class SubscriptionsController {

    @GetMapping("/new", "/")
    fun new(viewModel: Model): String {
        viewModel.addAttribute("subscription", Subscription())
        return "subscribetonewsletter/new"
    }

    @PostMapping
    fun create(@ModelAttribute subscription: Subscription, viewModel: Model): String {
        subscription.email = "submitted"
        viewModel.addAttribute("subscription", subscription)
        return "subscribetonewsletter/new"
    }
}