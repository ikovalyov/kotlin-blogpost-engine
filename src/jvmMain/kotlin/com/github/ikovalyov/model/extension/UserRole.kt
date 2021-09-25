package com.github.ikovalyov.model.extension

import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.security.UserRole
import kotlinx.datetime.Instant
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

object UserRoleExtension {
    fun UserRole.toDynamoDbMap(): Map<String, AttributeValue> {
        return mapOf(
            "id" to AttributeValue.builder().s(id.toString()).build(),
            "name" to AttributeValue.builder().s(name).build()
        )
    }

    fun UserRole.Companion.fromDynamoDbMap(map: Map<String, AttributeValue>): UserRole {
        return UserRole(
            uuidFrom(map["id"]!!.s()),
            map["name"]!!.s()
        )
    }
}