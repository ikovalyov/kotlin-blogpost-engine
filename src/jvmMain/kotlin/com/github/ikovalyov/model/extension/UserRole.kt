package com.github.ikovalyov.model.extension

import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.security.UserRole
import kotlinx.datetime.Clock
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

object UserRoleExtension {
    fun UserRole.toDynamoDbMap(): Map<String, AttributeValue> {
        return mapOf(
            "id" to AttributeValue.builder().s(id.toString()).build(),
            "body" to AttributeValue.builder().s(name).build(),
            "lastModified" to AttributeValue.builder().n(lastModified.epochSeconds.toString()).build()
        )
    }

    fun UserRole.Companion.fromDynamoDbMap(map: Map<String, AttributeValue>): UserRole {
        return UserRole(
            uuidFrom(map["id"]!!.s()),
            Clock.System.now(),
            map["body"]!!.s()
        )
    }
}
