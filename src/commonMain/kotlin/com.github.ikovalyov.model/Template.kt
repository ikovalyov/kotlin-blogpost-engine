package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.markers.BodyInterface
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.markers.NamedInterface
import com.github.ikovalyov.model.markers.TimedInterface
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.serializers.InstantIso8601Serializer
import kotlinx.datetime.toInstant
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class Template(
    @Serializable(with = UuidSerializer::class) override val id: Uuid,
    override val name: String,
    override val body: String,
    override val lastModified: Instant = Clock.System.now()
) : IEditable<Template>, TimedInterface, NamedInterface, BodyInterface {
  companion object {
      const val id = "id"
      const val name = "name"
      const val body = "body"
      const val lastModified = "lastModified"

    fun create(id: Uuid, name: String, body: String) = Template(id, name, body, Clock.System.now())
  }

  override val preview: String
    get() {
      return body.substring(0, 255)
    }

    override fun <F : Any> updateField(field: IEditable.EditableMetadata<F>, fieldValue: F): Template {
        return when (field.fieldName) {
            Companion.id -> copy(id = fieldValue as Uuid)
            Companion.name -> copy(name = fieldValue as String)
            Companion.body -> copy(body = fieldValue as String)
            Companion.lastModified -> copy(lastModified = fieldValue as Instant)
            else -> throw IllegalStateException("Unknown field name {$field.fieldName}")
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <F : Any> getFieldValue(field: IEditable.EditableMetadata<F>): F {
        return when (field.fieldName) {
            Companion.id -> id as F
            Companion.name -> name as F
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
                it.toString()
            },
            deserialize = {
                uuidFrom(it)
            }),
        IEditable.EditableMetadata(
            fieldName = Companion.name,
            fieldType = String::class,
            readOnly = false,
            serialize = {
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
                if (it.isEmpty()) {
                    Clock.System.now()
                } else {
                    it.toInstant()
                }
            }),
    )

    override fun serialize(): String {
        return Json.encodeToString(this)
    }
}
