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
data class Article(override val id: Uuid, val name: String, val abstract: String, val body: String, val author: Uuid, val tags: List<String>?, val meta: List<String>?, val template: Uuid?) : IEditable {
    override fun getMetadata(): List<IEditable.EditableMetadata<*, Article>> = listOf(
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
            fieldName = "Name",
            predefinedList = null,
        ),
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.Abstract,
            readOnly = false,
            serialize = {
                it
            },
            deserialize = {
                it
            },
            update = {
                copy(abstract = it)
            },
            get = {
                abstract
            },
            fieldName = "Abstract",
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
            fieldType = IEditable.FieldType.Tags,
            readOnly = false,
            serialize = {
                it.joinToString(separator = ",")
            },
            deserialize = {
                it.split(",")
            },
            update = {
                copy(tags = it)
            },
            get = {
                tags
            },
            fieldName = "Tags",
            predefinedList = null,
        ),
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.StringListFiledType,
            readOnly = false,
            serialize = {
                it.joinToString(separator = ",")
            },
            deserialize = {
                it.split(",")
            },
            update = {
                copy(meta = it)
            },
            get = {
                meta
            },
            fieldName = "Meta",
            predefinedList = null,
        ),
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.Template,
            readOnly = false,
            serialize = {
                it.toString()
            },
            deserialize = { uuid ->
                uuidFrom(uuid)
            },
            update = {
                copy(template = it)
            },
            get = {
                template
            },
            fieldName = "Template",
            predefinedList = null,
        ),
    )

    override fun serialize(): String = Json.encodeToString(this)
}
