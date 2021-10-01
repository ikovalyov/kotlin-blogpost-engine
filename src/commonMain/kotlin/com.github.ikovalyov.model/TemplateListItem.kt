package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TemplateListItem(
    @Serializable(with = UuidSerializer::class) val id: Uuid,
    val lastModified: Instant
) {
    companion object {
        fun fromTemplate(template: Template): TemplateListItem {
            return TemplateListItem(template.id, template.lastModified)
        }
    }
}
