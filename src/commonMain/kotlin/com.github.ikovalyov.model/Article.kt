@file:OptIn(ExperimentalSerializationApi::class)
@file:UseSerializers(UuidSerializer::class)

package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.security.User
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Article(
    override val id: Uuid,
    val name: String,
    val abstract: String,
    val body: String,
    val author: User,
    val tags: List<String>?,
    val meta: List<String>?,
    val template: Uuid?, // Template uuid
    val getUserCallBack: suspend (Uuid) -> User
) : IEditable {
    override fun getMetadata(): List<IEditable.EditableMetadata<*, out IEditable>> {
        return listOf(
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
                }
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
                }
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
                }
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
                }
            ),
            IEditable.EditableMetadata(
                fieldType = IEditable.FieldType.Author,
                readOnly = false,
                serialize = {
                    it.toString()
                },
                deserialize = {
                    getUserCallBack(uuidFrom(it))
                },
                update = {
                    copy(author = it)
                },
                get = {
                    author
                }
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
                }
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
                }
            ),
            IEditable.EditableMetadata(
                fieldType = IEditable.FieldType.Id,
                readOnly = false,
                serialize = {
                    it.toString()
                },
                deserialize = {
                    uuidFrom(it)
                },
                update = {
                    copy(template = it)
                },
                get = {
                    template
                }
            )
        )
    }

    override fun serialize(): String {
        return Json.encodeToString(this)
    }
}
