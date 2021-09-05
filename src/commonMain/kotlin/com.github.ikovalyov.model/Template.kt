package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.extension.UuidSerializer
import com.github.ikovalyov.model.markers.BodyInterface
import com.github.ikovalyov.model.markers.IdInterface
import com.github.ikovalyov.model.markers.NamedInterface
import com.github.ikovalyov.model.markers.TimedInterface
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class Template(
    @Serializable( with = UuidSerializer::class ) override val id: Uuid,
    override val name: String,
    override val body: String,
    override val lastModified: Instant = Clock.System.now()
): IdInterface, TimedInterface, NamedInterface, BodyInterface<String> {
  companion object {
    fun create(id: Uuid, name: String, body: String) = Template(id, name, body, Clock.System.now())
  }
}
