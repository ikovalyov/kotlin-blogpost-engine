package com.github.ikovalyov.model.markers

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.security.Email
import com.github.ikovalyov.model.security.Password
import kotlinx.datetime.Instant

interface IEditable<T : IEditable<T>> {
    fun <F : Any> updateField(field: EditableMetadata<F, T>, serializedData: String): T {
        val data = field.deserialize(serializedData)
        return field.update(data)
    }

    fun <F : Any> getFieldValueAsString(field: EditableMetadata<F, T>): String {
        val fieldValue = field.get()
        return field.serialize(fieldValue)
    }

    sealed class FieldType<T : Any> {
        object Id : FieldType<Uuid>()
        object LastModified : FieldType<Instant>()
        object Body : FieldType<String>()
        object Name : FieldType<String>()
        object Email : FieldType<com.github.ikovalyov.model.security.Email>()
        object Nickname : FieldType<String>()
        object UserRoles : FieldType<List<Uuid>>()
        object Password : FieldType<com.github.ikovalyov.model.security.Password>()
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
