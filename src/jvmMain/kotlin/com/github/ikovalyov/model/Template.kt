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
}
