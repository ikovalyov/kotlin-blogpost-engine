package com.github.ikovalyov.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class Template(
    val id: String, val template: String, val lastModified: Instant = Clock.System.now()
) {
    companion object {
        fun create(id: String, template: String) = Template(id, template, Clock.System.now())
    }
}
