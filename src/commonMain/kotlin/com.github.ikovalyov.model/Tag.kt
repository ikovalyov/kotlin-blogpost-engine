package com.github.ikovalyov.model

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.markers.IEditable

data class Tag(
    override val id: Uuid,
    val name: String
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
                },
                fieldName = "Id",
                predefinedList = null
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
                predefinedList = null
            )
        )
    }

    override fun serialize(): String {
        TODO("Not yet implemented")
    }

}