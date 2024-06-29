@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
@file:UseSerializers(UuidSerializer::class)

package com.github.ikovalyov.model.security

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class UserRole(override val id: Uuid, val lastModified: Instant, val name: String) : IEditable {
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val LAST_MODIFIED = "lastModified"
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
            },
            fieldName = ID,
            predefinedList = null,
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
            },
            fieldName = NAME,
            predefinedList = null,
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
                    Instant.parse(it)
                }
            },
            get = {
                lastModified
            },
            update = {
                copy(lastModified = it)
            },
            fieldName = LAST_MODIFIED,
            predefinedList = null,
        ),
    )

    override fun serialize(): String = Json.encodeToString(this)
}
