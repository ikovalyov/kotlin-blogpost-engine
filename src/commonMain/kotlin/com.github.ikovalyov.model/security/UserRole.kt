package com.github.ikovalyov.model.security

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class UserRole(
    @Serializable(with = UuidSerializer::class) override val id: Uuid,
    override val lastModified: Instant,
    override val body: String
): IEditable<UserRole> {
    companion object {
        const val id = "id"
        const val body = "body"
        const val lastModified = "lastModified"
    }

    override fun <F : Any> updateField(field: IEditable.EditableMetadata<F>, fieldValue: F): UserRole {
        return when (field.fieldName) {
            Companion.id -> copy(id = fieldValue as Uuid)
            Companion.body -> copy(body = fieldValue as String)
            Companion.lastModified -> copy(lastModified = fieldValue as Instant)
            else -> throw IllegalStateException("Unknown field name {$field.fieldName}")
        }
    }

    override fun <F : Any> getFieldValue(field: IEditable.EditableMetadata<F>): F {
        return when (field.fieldName) {
            Companion.id -> id as F
            Companion.body -> body as F
            Companion.lastModified -> lastModified as F
            else -> throw IllegalStateException("Unknown field name {$field.fieldName}")
        }
    }

    override fun getMetadata(): List<IEditable.EditableMetadata<*>> = listOf(
        IEditable.EditableMetadata(
            fieldName = Companion.id,
            fieldType = Uuid::class,
            readOnly = true,
            serialize = {
                id.toString()
            },
            deserialize = {
                uuidFrom(it)
            }
        ),
        IEditable.EditableMetadata(
            fieldName = Companion.body,
            fieldType = String::class,
            readOnly = false,
            serialize = { it },
            deserialize = { it }
        ),

        IEditable.EditableMetadata(
            fieldName = Companion.lastModified,
            fieldType = Instant::class,
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
            }
        ),
    )

    override fun serialize(): String {
        return Json.encodeToString(this)
    }
}
