package com.github.ikovalyov.model.security

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.serialization.Serializable

@Serializable
data class User(
    @Serializable(with = UuidSerializer::class) override val id: Uuid,
    val email: Email,
    val loggedIn: Boolean,
    val nickname: String,
    val roles: List<UserRole>,
    val password: Password
) : IEditable<User> {
    override fun getMetadata(): List<IEditable.EditableMetadata<*, User>> {
        TODO("Not yet implemented")
    }

    override fun serialize(): String {
        TODO("Not yet implemented")
    }
}
