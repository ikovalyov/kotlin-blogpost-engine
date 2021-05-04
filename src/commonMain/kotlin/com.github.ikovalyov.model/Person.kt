package com.github.ikovalyov.model

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@ExperimentalJsExport @JsExport data class Person(val username: String, val loggedIn: Boolean)
