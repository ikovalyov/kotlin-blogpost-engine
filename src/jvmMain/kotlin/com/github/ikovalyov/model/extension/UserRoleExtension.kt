package com.github.ikovalyov.model.extension

import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.security.UserRole
import kotlinx.datetime.Clock
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

object UserRoleExtension {
    val fieldName = "name"

    fun UserRole.toDynamoDbMap(): Map<String, AttributeValue> = mapOf(
        "id" to AttributeValue.builder().s(id.toString()).build(),
        fieldName to AttributeValue.builder().s(name).build(),
        "lastModified" to AttributeValue.builder().n(lastModified.epochSeconds.toString()).build(),
    )

    fun UserRole.Companion.fromDynamoDbMap(map: Map<String, AttributeValue>): UserRole = UserRole(
        uuidFrom(map["id"]!!.s()),
        Clock.System.now(),
        map[fieldName]!!.s(),
    )
}
