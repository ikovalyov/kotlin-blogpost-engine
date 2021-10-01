package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.markers.IEditable
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


data class Item(override val id: Uuid,
                override val lastModified: Instant,
                override val body: String
) : IEditable<Item> {

    companion object {
        const val id = "id"
        const val lastModified = "lastModified"
        const val body = "body"
    }

    override fun <F : Any> updateField(field: IEditable.EditableMetadata<F>, fieldValue: F): Item {
        return when(field.fieldName) {
            Companion.id -> copy(id = fieldValue as Uuid)
            Companion.lastModified -> copy(lastModified = fieldValue as Instant)
            Companion.body -> copy(body = fieldValue as String)
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
            fieldType = String::class,
            readOnly = true,
            serialize =  {
                it
            },
            deserialize = {
                it
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
                Instant.parse(it)
            }
        ),
    )

    override fun serialize(): String {
        return Json.encodeToString(this)
    }
}
