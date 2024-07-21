package com.github.ikovalyov.infrastructure.dynamodb.repository.articles

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.infrastructure.dynamodb.converter.CommentConverter
import com.github.ikovalyov.infrastructure.dynamodb.repository.CrudRepository
import com.github.ikovalyov.model.Comment
import jakarta.inject.Inject
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
class CommentsRepository(dynamoDbClient: DynamoDbAsyncClient) : CrudRepository<Comment>(dynamoDbClient) {
    @Inject
    private lateinit var commentConverter: CommentConverter

    companion object {
        const val TABLE_NAME = "comment"
    }

    override val tableName = TABLE_NAME

    suspend fun list(): List<Comment> = list {
        commentConverter.fromDynamoDB(it)
    }

    suspend fun insert(item: Comment): Boolean = insert(item) {
        commentConverter.toDynamoDB(it)
    }

    suspend fun update(item: Comment): Boolean = update(item) {
        commentConverter.toDynamoDB(it)
    }

    suspend fun get(id: Uuid): Comment? = get(id) {
        commentConverter.fromDynamoDB(it)
    }
}
