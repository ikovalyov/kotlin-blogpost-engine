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
data class Comment(override val id: Uuid, val body: String, val author: Uuid, val article: Uuid) : IEditable {
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
            fieldName = "Id",
            predefinedList = null,
        ),
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.Body,
            readOnly = false,
            serialize = {
                it
            },
            deserialize = {
                it
            },
            update = {
                copy(body = it)
            },
            get = {
                body
            },
            fieldName = "Body",
            predefinedList = null,
        ),
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.Author,
            readOnly = false,
            serialize = {
                it.toString()
            },
            deserialize = { uuid ->
                uuidFrom(uuid)
            },
            update = {
                copy(author = it)
            },
            get = {
                author
            },
            fieldName = "Author",
            predefinedList = null,
        ),
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.Article,
            readOnly = false,
            serialize = {
                it.toString()
            },
            deserialize = { uuid ->
                uuidFrom(uuid)
            },
            update = {
                copy(article = it)
            },
            get = {
                article
            },
            fieldName = "Article",
            predefinedList = null,
        ),
    )

    override fun serialize(): String = Json.encodeToString(this)
}
