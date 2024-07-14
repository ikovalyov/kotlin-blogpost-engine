@file:UseSerializers(UuidSerializer::class)

package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Tag(override val id: Uuid, val name: String) : IEditable {
    companion object {
        const val ID = "id"
        const val NAME = "name"

        fun create(id: Uuid, name: String) = Tag(id, name)
    }

    override fun getMetadata(): List<IEditable.EditableMetadata<*, out IEditable>> = listOf(
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
            get = {
                id
            },
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
            get = {
                name
            },
            fieldName = NAME,
            predefinedList = null,
        ),
    )

    override fun serialize(): String = Json.encodeToString(this)
}
