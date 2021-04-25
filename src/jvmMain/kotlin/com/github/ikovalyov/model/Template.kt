package com.github.ikovalyov.model

import com.fasterxml.jackson.annotation.JsonCreator
import java.time.Instant
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

data class Template(
    val id: String, val template: String, val lastModified: Instant = Instant.now()
) {
    companion object {
        @JsonCreator
        fun create(id: String, template: String) = Template(id, template, Instant.now())

        fun fromDynamoDbMap(map: Map<String, AttributeValue>): Template {
            return Template(
                map["id"]!!.s(), map["template"]!!.s(), Instant.parse(map["lastModified"]!!.s()))
        }
    }

    fun toDynamoDbMap(): Map<String, AttributeValue> {
        return mapOf(
            "id" to AttributeValue.builder().s(id).build(),
            "template" to AttributeValue.builder().s(template).build(),
            "lastModified" to AttributeValue.builder().s(lastModified.toString()).build())
    }
}
