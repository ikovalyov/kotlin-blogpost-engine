package com.github.ikovalyov.model.extension

import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.Article
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

object ArticleExtension {
    fun Article.toDynamoDbMap(): Map<String, AttributeValue> {
        return mapOf(
            "id" to AttributeValue.builder().s(id.toString()).build(),
            "name" to AttributeValue.builder().s(name.toString()).build(),
            "abstract" to AttributeValue.builder().s(abstract).build(),
            "body" to AttributeValue.builder().s(body).build(),
            "author" to AttributeValue.builder().s(author.toString()).build(),
            "tags" to AttributeValue.builder().ss(tags).build(),
            "meta" to AttributeValue.builder().ss(meta).build(),
            "template" to AttributeValue.builder().s(template.toString()).build()
        )
    }

    fun Article.Companion.fromDynamoDbMap(map: Map<String, AttributeValue>): Article {
        return Article(
            id = uuidFrom(map["id"]!!.s()),
            name = map["name"]!!.s(),
            abstract = map["abstract"]!!.s(),
            body = map["body"]!!.s(),
            author = uuidFrom(map["author"]!!.s()),
            tags = map["tags"]!!.ss(),
            meta = map["meta"]!!.ss(),
            template = uuidFrom(map["template"]!!.s())
        )
    }
}
