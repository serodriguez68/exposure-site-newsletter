package com.serodriguez.exposuresitenewsletter.base

/* FIXME: how to replace value: Any with a stricter type or a generic */
data class ValidationError(val property: String, val value: Any? = null, val message: String)