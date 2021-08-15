package com.github.ikovalyov.model.extension

import com.github.ikovalyov.model.Template
import kotlinx.datetime.Instant
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

fun Template.toDynamoDbMap(): Map<String, AttributeValue> {
  return mapOf(
      "id" to AttributeValue.builder().s(id).build(),
      "template" to AttributeValue.builder().s(template).build(),
      "lastModified" to AttributeValue.builder().s(lastModified.toString()).build())
}

fun Template.Companion.fromDynamoDbMap(map: Map<String, AttributeValue>): Template {
  return Template(map["id"]!!.s(), map["template"]!!.s(), Instant.parse(map["lastModified"]!!.s()))
}
