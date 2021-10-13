package com.github.ikovalyov.model.markers

import com.benasher44.uuid.Uuid
import kotlinx.datetime.Instant
import kotlin.reflect.KClass

interface IEditable<T : IEditable<T>> {
    fun <F : Any> updateField(field: EditableMetadata<F, T>, serializedData: String): T {
        val data = field.deserialize(serializedData)
        return field.update(data)
    }

    fun <F : Any> getFieldValueAsString(field: EditableMetadata<F, T>): String {
        val fieldValue = field.get()
        return field.serialize(fieldValue)
    }

    sealed class FieldType<T: Any> {
        object Id : FieldType<Uuid>()
        object LastModified : FieldType<Instant>()
        object Body : FieldType<String>()
        object Name : FieldType<String>()
    }

    data class EditableMetadata<F : Any, T : IEditable<T>>(
        val fieldType: FieldType<F>,
        val readOnly: Boolean,
        val serialize: (F) -> String,
        val deserialize: (String) -> F,
        val update: (F) -> T,
        val get: () -> F
    )

    val id: Uuid
    fun getMetadata(): List<EditableMetadata<*, T>>
    fun serialize(): String
}
