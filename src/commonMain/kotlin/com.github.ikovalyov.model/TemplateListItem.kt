package com.github.ikovalyov.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TemplateListItem(val id: String, val lastModified: Instant) {
    companion object {
        fun fromTemplate(template: Template): TemplateListItem {
            return TemplateListItem(template.id, template.lastModified)
        }
    }
}
