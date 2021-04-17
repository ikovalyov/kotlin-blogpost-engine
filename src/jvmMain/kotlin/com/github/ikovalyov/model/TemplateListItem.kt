package com.github.ikovalyov.model

import java.time.Instant

data class TemplateListItem(val id: String, val lastModified: Instant) {
    companion object {
        fun fromTemplate(template: Template): TemplateListItem {
            return TemplateListItem(template.id, template.lastModified)
        }
    }
}
