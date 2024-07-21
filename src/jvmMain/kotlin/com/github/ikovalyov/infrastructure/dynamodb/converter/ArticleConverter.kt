package com.github.ikovalyov.infrastructure.dynamodb.converter

import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.infrastructure.dynamodb.DynamodbConverterInterface
import com.github.ikovalyov.model.Article
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Singleton
class ArticleConverter : DynamodbConverterInterface<Article> {
    override suspend fun fromDynamoDB(map: Map<String, AttributeValue>): Article {
        val templateUuid = map["template"]?.let {
            uuidFrom(it.s())
        }
        return Article(
            id = uuidFrom(map["id"]!!.s()),
            name = map["name"]!!.s(),
            abstract = map["abstract"]!!.s(),
            body = map["body"]!!.s(),
            author = uuidFrom(map["author"]!!.s()),
            tags = map["tags"]?.ss(),
            meta = map["meta"]?.ss(),
            template = templateUuid,
        )
    }

    override suspend fun toDynamoDB(item: Article): Map<String, AttributeValue> {
        val mutableMap = mutableMapOf<String, AttributeValue>(
            "id" to AttributeValue.builder().s(item.id.toString()).build(),
            "name" to AttributeValue.builder().s(item.name).build(),
            "abstract" to AttributeValue.builder().s(item.abstract).build(),
            "body" to AttributeValue.builder().s(item.body).build(),
            "author" to AttributeValue.builder().s(item.author.toString()).build(),
            "template" to AttributeValue.builder().s(item.template.toString()).build(),
        )
        if (item.meta !== null && item.meta.isNotEmpty()) {
            mutableMap["meta"] = AttributeValue.builder().ss(item.meta).build()
        }
        if (item.tags !== null && item.tags.isNotEmpty()) {
            mutableMap["tags"] = AttributeValue.builder().ss(item.tags).build()
        }
        return mutableMap
    }
}
