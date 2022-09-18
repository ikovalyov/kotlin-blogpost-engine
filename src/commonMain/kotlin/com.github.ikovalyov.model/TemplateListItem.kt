@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
@file:UseSerializers(UuidSerializer::class)

package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers

@Serializable
data class TemplateListItem(
    val id: Uuid,
    val lastModified: Instant
) {
    companion object {
        fun fromTemplate(template: Template): TemplateListItem {
            return TemplateListItem(template.id, template.lastModified)
        }
    }
}
