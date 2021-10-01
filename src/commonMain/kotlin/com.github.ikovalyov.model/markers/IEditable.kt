package com.github.ikovalyov.model.markers

import com.benasher44.uuid.Uuid
import kotlin.reflect.KClass
import kotlinx.datetime.Instant

interface IEditable<T: IEditable<T>> {
    fun <F: Any>updateField(field: EditableMetadata<F>, serializedData: String) : T {
        val data = field.deserialize(serializedData)
        return updateField(field = field, fieldValue = data)
    }

    fun <F: Any>getFieldValueAsString(field: EditableMetadata<F>): String {
        val fieldValue = getFieldValue(field)
        return field.serialize(fieldValue)
    }

    data class EditableMetadata<T: Any>(
        val fieldName: String,
        val fieldType: KClass<T>,
        val readOnly: Boolean,
        val serialize: (T) -> String,
        val deserialize: (String) -> T
    )

    val id: Uuid
    val lastModified: Instant
    val body: String
    fun <F: Any>updateField(field: EditableMetadata<F>, fieldValue: F): T
    fun <F: Any>getFieldValue(field: EditableMetadata<F>): F
    fun getMetadata(): List<EditableMetadata<*>>
    fun serialize(): String
}