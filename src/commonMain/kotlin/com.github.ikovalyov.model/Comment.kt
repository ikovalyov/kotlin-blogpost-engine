@file:UseSerializers(UuidSerializer::class)

package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.security.User
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Comment(override val id: Uuid, val body: String, val author: User, val article: Article, val userList: List<User>, val articleList: List<Article>) : IEditable {
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
            readOnly = true,
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
                it.id.toString()
            },
            deserialize = { uuid ->
                userList.first {
                    it.id.toString() == uuid
                }
            },
            update = {
                copy(author = it)
            },
            get = {
                author
            },
            fieldName = "Author",
            predefinedList = userList,
        ),
        IEditable.EditableMetadata(
            fieldType = IEditable.FieldType.Article,
            readOnly = false,
            serialize = {
                it.id.toString()
            },
            deserialize = { uuid ->
                articleList.first {
                    it.id.toString() == uuid
                }
            },
            update = {
                copy(article = it)
            },
            get = {
                article
            },
            fieldName = "Author",
            predefinedList = articleList,
        ),
    )

    override fun serialize(): String = Json.encodeToString(this)
}
