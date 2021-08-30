package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class TemplateListItem(@Contextual val id: Uuid, val lastModified: Instant) {
  companion object {
    fun fromTemplate(template: Template): TemplateListItem {
      return TemplateListItem(template.id, template.lastModified)
    }
  }
}
