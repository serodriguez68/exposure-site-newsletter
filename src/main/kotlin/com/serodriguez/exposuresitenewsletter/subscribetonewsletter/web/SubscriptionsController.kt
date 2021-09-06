package com.serodriguez.exposuresitenewsletter.subscribetonewsletter.web

import arrow.core.Nel
import com.serodriguez.exposuresitenewsletter.base.ValidationError
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.GetWatchableSuburbs
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.NewSubscriptionDTO
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.SubscribeAndWatch
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.SubscribeAndWatchError
import com.serodriguez.exposuresitenewsletter.subscribetonewsletter.usecases.SubscriberData
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView

@Controller
@RequestMapping("/", "/subscriptions")
class SubscriptionsController(
    @Autowired val subscribeAndWatch: SubscribeAndWatch,
    @Autowired val getWatchableSuburbs: GetWatchableSuburbs,
) {

    @GetMapping("/new", "/")
    fun new(): ModelAndView {
        val modelAndView = ModelAndView("subscribetonewsletter/new")
        modelAndView.addObject(
            "newSubscriptionData",
            NewSubscriptionDTO(SubscriberData())
        )
        modelAndView.addObject("watchableSuburbs", getWatchableSuburbs.call())
        return modelAndView
    }

    @PostMapping
    fun create(@ModelAttribute newSubscriptionDTO: NewSubscriptionDTO, bindingResult: BindingResult):
        ModelAndView = runBlocking {
        subscribeAndWatch.call(newSubscriptionDTO).fold(
            { error ->
                when (error) {
                    is SubscribeAndWatchError.NotValid -> {
                        addValidationErrorsToBindingResult(error.reasons, bindingResult)
                        val modelAndView = ModelAndView("subscribetonewsletter/new")
                        modelAndView.addObject("newSubscriptionData", newSubscriptionDTO)
                        modelAndView.addObject("watchableSuburbs", getWatchableSuburbs.call())
                        modelAndView.status = HttpStatus.UNPROCESSABLE_ENTITY
                        modelAndView
                    }
                    SubscribeAndWatchError.NotAuthorized -> TODO() // will not be used. Just for demo purposes
                }
            },
            {
                val modelAndView = ModelAndView("redirect:/thank-you")
                modelAndView.addObject("email", it.email)
                modelAndView
            }
        )
    }

    @GetMapping("/thank-you")
    fun thankYou(@RequestParam(value = "email", required = true) email: String): ModelAndView {
        val modelAndView = ModelAndView("subscribetonewsletter/thank-you")
        modelAndView.addObject("email", email)
        return modelAndView
    }

    /* TODO: move this out to a general helper fun */
    /* MUTATES the given bindingResult to add the given list of validation errors */
    private fun addValidationErrorsToBindingResult(
        errors: Nel<ValidationError>, bindingResult:
        BindingResult
    ) {
        /* TODO: add mapping for global errors */
        // bindingResult.reject("whole.error.code", "object error")
        errors.forEach { validationError ->
            bindingResult.rejectValue(
                validationError.property,
                "error.code.is.unused",
                validationError.message
            )
        }
        logger.info("${bindingResult.allErrors}")
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(SubscriptionsController::class.java)
    }
}