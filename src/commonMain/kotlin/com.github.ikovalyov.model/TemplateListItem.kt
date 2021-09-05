package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.extension.UuidSerializer
import com.github.ikovalyov.model.markers.IdInterface
import com.github.ikovalyov.model.markers.TimedInterface
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TemplateListItem(
  @Serializable( with = UuidSerializer::class ) override val id: Uuid,
  override val lastModified: Instant
): IdInterface, TimedInterface {
  companion object {
    fun fromTemplate(template: Template): TemplateListItem {
      return TemplateListItem(template.id, template.lastModified)
    }
  }
}
