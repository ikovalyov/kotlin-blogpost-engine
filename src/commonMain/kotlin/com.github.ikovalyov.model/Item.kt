@file:UseSerializers(UuidSerializer::class)

package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Item(override val id: Uuid, val lastModified: Instant, val body: String) : IEditable {

    companion object {
        const val ID = "id"
        const val LAST_MODIFIED = "lastModified"
        const val BODY = "body"
    }

    override fun getMetadata(): List<IEditable.EditableMetadata<*, Item>> = listOf(
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
                Instant.parse(it)
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
