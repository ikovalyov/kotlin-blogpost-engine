package com.github.ikovalyov.model

import io.micronaut.core.annotation.Introspected

@Introspected data class Item(val id: Int, val content: String)
