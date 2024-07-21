package com.github.ikovalyov.infrastructure.dynamodb.converter

import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.infrastructure.dynamodb.DynamodbConverterInterface
import com.github.ikovalyov.model.Comment
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Singleton
class CommentConverter : DynamodbConverterInterface<Comment> {
    override suspend fun fromDynamoDB(map: Map<String, AttributeValue>): Comment = Comment(
        id = uuidFrom(map["id"]!!.s()),
        body = map["body"]!!.s(),
        author = uuidFrom(map["author"]!!.s()),
        article = uuidFrom(map["article"]!!.s()),
    )

    override suspend fun toDynamoDB(item: Comment): Map<String, AttributeValue> {
        val mutableMap = mutableMapOf<String, AttributeValue>(
            "id" to AttributeValue.builder().s(item.id.toString()).build(),
            "body" to AttributeValue.builder().s(item.body).build(),
            "author" to AttributeValue.builder().s(item.author.toString()).build(),
            "article" to AttributeValue.builder().s(item.article.toString()).build(),
        )
        return mutableMap
    }
}
