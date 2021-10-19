@file:UseSerializers(UuidSerializer::class)

package com.github.ikovalyov.model.security

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.markers.IEditable
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ExperimentalSerializationApi
@Serializable
data class User(
    override val id: Uuid,
    val email: Email,
    val loggedIn: Boolean,
    val nickname: String,
    val roles: List<Uuid>,
    val password: Password
) : IEditable<User> {
    override fun getMetadata(): List<IEditable.EditableMetadata<*, User>> {
        return listOf(
            IEditable.EditableMetadata(
                fieldType = IEditable.FieldType.Id,
                readOnly = true,
                serialize = { id.toString() },
                deserialize = { uuidFrom(it) },
                get = { id },
                update = { copy(id = it) }
            ),
            IEditable.EditableMetadata(
                fieldType = IEditable.FieldType.Email,
                readOnly = true,
                serialize = { it.value.value },
                deserialize = { Email(ShortString(it)) },
                get = { email },
                update = { copy(email = it) }
            ),
            IEditable.EditableMetadata(
                fieldType = IEditable.FieldType.Nickname,
                readOnly = true,
                serialize = { it },
                deserialize = { it },
                get = { nickname },
                update = { copy(nickname = it) }
            ),
            IEditable.EditableMetadata(
                fieldType = IEditable.FieldType.UserRoles,
                readOnly = true,
                serialize = { Json.encodeToString(ListSerializer(UuidSerializer), it) },
                deserialize = { Json.decodeFromString(ListSerializer(UuidSerializer), it) },
                get = { roles },
                update = { copy(roles = it) }
            ),
            IEditable.EditableMetadata(
                fieldType = IEditable.FieldType.Password,
                readOnly = true,
                serialize = { it.value.value },
                deserialize = { Password(ShortString(it)) },
                get = { password },
                update = { copy(password = it) }
            ),
        )
    }

    override fun serialize(): String {
        return Json.encodeToString(this)
    }
}
