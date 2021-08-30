package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.extension.UuidSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Template(
    @Serializable( with = UuidSerializer::class ) val id: Uuid,
    val name: String,
    val body: String,
    val lastModified: Instant = Clock.System.now()
) {
  companion object {
    fun create(id: Uuid, name: String, body: String) = Template(id, name, body, Clock.System.now())
  }
}
