@file:UseSerializers(UuidSerializer::class)

package com.github.ikovalyov.model

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
data class Template(override val id: Uuid, val name: String, val body: String, val lastModified: Instant = Clock.System.now()) : IEditable {
    companion object {
        const val ID = "id"
        const val NAME = "name"
        const val BODY = "body"
        const val LAST_MODIFIED = "lastModified"

        fun create(id: Uuid, name: String, body: String) = Template(id, name, body, Clock.System.now())
    }

    override fun getMetadata(): List<IEditable.EditableMetadata<*, Template>> = listOf(
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.Id,
            readOnly = true,
            serialize = {
                it.toString()
            },
            deserialize = {
                uuidFrom(it)
            },
            update = {
                copy(id = it)
            },
            get = { id },
            fieldName = ID,
            predefinedList = null,
        ),
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.Name,
            readOnly = false,
            serialize = {
                it
            },
            deserialize = {
                it
            },
            update = {
                copy(name = it)
            },
            get = { name },
            fieldName = NAME,
            predefinedList = null,
        ),
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.Body,
            readOnly = false,
            serialize = { it },
            deserialize = { it },
            update = {
                copy(body = it)
            },
            get = { body },
            fieldName = BODY,
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
            update = {
                copy(lastModified = it)
            },
            get = { lastModified },
            fieldName = LAST_MODIFIED,
            predefinedList = null,
        ),
    )

    override fun serialize(): String = Json.encodeToString(this)
}
