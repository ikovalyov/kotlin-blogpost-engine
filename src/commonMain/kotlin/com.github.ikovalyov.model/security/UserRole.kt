package com.github.ikovalyov.model.security

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class UserRole(
    @Serializable(with = UuidSerializer::class) override val id: Uuid,
    override val lastModified: Instant,
    override val body: String
): IEditable<UserRole> {

    override fun <F : Any> updateField(field: IEditable.EditableMetadata<F>, fieldValue: F): UserRole {
        TODO("Not yet implemented")
    }

    override fun <F : Any> getFieldValue(field: IEditable.EditableMetadata<F>): F {
        TODO("Not yet implemented")
    }

    override fun getMetadata(): List<IEditable.EditableMetadata<*>> {
        TODO("Not yet implemented")
    }

    override fun serialize(): String {
        TODO("Not yet implemented")
    }
}
