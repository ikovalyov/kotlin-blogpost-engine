package com.github.ikovalyov.model.security

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.markers.IdInterface
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.serialization.Serializable

@Serializable
data class UserRole(@Serializable(with = UuidSerializer::class) override val id: Uuid, val name: String): IdInterface {
    companion object
}
