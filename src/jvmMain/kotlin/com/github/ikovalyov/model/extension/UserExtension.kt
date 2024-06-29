package com.github.ikovalyov.model.extension

import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.security.Email
import com.github.ikovalyov.model.security.Password
import com.github.ikovalyov.model.security.ShortString
import com.github.ikovalyov.model.security.User
import com.github.ikovalyov.model.serializer.UuidSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@ExperimentalSerializationApi
object UserExtension {
    fun User.toDynamoDbMap(): Map<String, AttributeValue> = mapOf(
        "id" to AttributeValue.builder().s(id.toString()).build(),
        "email" to AttributeValue.builder().s(email.toString()).build(),
        "loggedIn" to AttributeValue.builder().bool(loggedIn).build(),
        "nickname" to AttributeValue.builder().s(nickname).build(),
        "roles" to AttributeValue.builder().s(
            Json.encodeToString(ListSerializer(UuidSerializer), roles),
        ).build(),
        "password" to AttributeValue.builder().s(password.toString()).build(),
    )

    fun User.Companion.fromDynamoDbMap(map: Map<String, AttributeValue>): User = User(
        id = uuidFrom(map["id"]!!.s()),
        email = Email(ShortString(map["email"]!!.s())),
        loggedIn = map["loggedIn"]!!.bool(),
        password = Password(ShortString(map["password"]!!.s())),
        nickname = map["nickname"]!!.s(),
        roles = Json.decodeFromString(ListSerializer(UuidSerializer), map["roles"]!!.s()),
    )
}
