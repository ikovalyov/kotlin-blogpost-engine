package com.github.ikovalyov.model.extension

import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.Template
import kotlinx.datetime.Instant
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun Template.toDynamoDbMap(): Map<String, AttributeValue> = mapOf(
    "id" to AttributeValue.builder().s(id.toString()).build(),
    "name" to AttributeValue.builder().s(name).build(),
    "body" to AttributeValue.builder().s(body).build(),
    "lastModified" to AttributeValue.builder().s(lastModified.toString()).build(),
)

fun Template.Companion.fromDynamoDbMap(map: Map<String, AttributeValue>): Template = Template(
    uuidFrom(map["id"]!!.s()),
    map["name"]!!.s(),
    map["body"]!!.s(),
    Instant.parse(map["lastModified"]!!.s()),
)
