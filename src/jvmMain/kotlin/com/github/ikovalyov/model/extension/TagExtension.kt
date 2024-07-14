package com.github.ikovalyov.model.extension

import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.Tag
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

object TagExtension {
    fun Tag.toDynamoDbMap(): Map<String, AttributeValue> = mapOf(
        "id" to AttributeValue.builder().s(id.toString()).build(),
        "name" to AttributeValue.builder().s(name).build(),
    )

    fun Tag.Companion.fromDynamoDbMap(map: Map<String, AttributeValue>): Tag = Tag(
        id = uuidFrom(map["id"]!!.s()),
        name = map["name"]!!.s(),
    )
}
