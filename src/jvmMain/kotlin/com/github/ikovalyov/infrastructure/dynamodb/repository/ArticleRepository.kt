package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.infrastructure.dynamodb.converter.DynamodbArticleConverter
import com.github.ikovalyov.model.Article
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
class ArticleRepository(dynamoDbClient: DynamoDbAsyncClient) : CrudRepository<Article>(dynamoDbClient) {

    @Inject private lateinit var articleConverter: DynamodbArticleConverter

    companion object {
        const val TABLE_NAME = "article"
    }

    override val tableName = TABLE_NAME

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun list(): List<Article> = list {
        articleConverter.fromDynamoDB(it)
    }

    suspend fun insert(item: Article): Boolean = insert(item) {
        articleConverter.toDynamoDB(it)
    }

    suspend fun update(item: Article): Boolean = update(item) {
        articleConverter.toDynamoDB(it)
    }

    suspend fun get(id: Uuid): Article? = get(id) {
        articleConverter.fromDynamoDB(it)
    }
}
