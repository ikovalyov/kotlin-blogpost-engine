@file:UseSerializers(UuidSerializer::class)

package com.github.ikovalyov.model.security

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class UserRole(
    override val id: Uuid,
    val lastModified: Instant,
    val name: String
) : IEditable<UserRole> {
    companion object {
        const val id = "id"
        const val name = "name"
        const val lastModified = "lastModified"
    }

    override fun getMetadata(): List<IEditable.EditableMetadata<*, UserRole>> = listOf(
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.Id,
            readOnly = true,
            serialize = {
                id.toString()
            },
            deserialize = {
                uuidFrom(it)
            },
            get = {
                id
            },
            update = {
                copy(id = it)
            }
        ),
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.Name,
            readOnly = false,
            serialize = { it },
            deserialize = { it },
            get = {
                name
            },
            update = {
                copy(name = it)
            }
        ),

        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.LastModified,
            readOnly = false,
            serialize = {
                it.toString()
            },
            deserialize = {
                if (it.isEmpty()) {
                    Clock.System.now()
                } else {
                    it.toInstant()
                }
            },
            get = {
                lastModified
            },
            update = {
                copy(lastModified = it)
            }
        )
    )

    override fun serialize(): String {
        return Json.encodeToString(this)
    }
}
