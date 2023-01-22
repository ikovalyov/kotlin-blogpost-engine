package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.infrastructure.dynamodb.converter.DynamodbArticleConverter
import com.github.ikovalyov.model.Article
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
@OptIn(ExperimentalSerializationApi::class)
class ArticleRepository(dynamoDbClient: DynamoDbAsyncClient) :
    CrudRepository<Article>(dynamoDbClient) {

    @Inject private lateinit var articleConverter: DynamodbArticleConverter

    companion object {
        const val tableName = "article"
    }

    override val tableName = ArticleRepository.tableName

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun list(): List<Article> {
        return list {
            articleConverter.fromDynamoDB(it)
        }
    }

    suspend fun insert(item: Article): Boolean {
        return insert(item) {
            articleConverter.toDynamoDB(it)
        }
    }

    suspend fun update(item: Article): Boolean {
        return update(item) {
            articleConverter.toDynamoDB(it)
        }
    }

    suspend fun get(id: Uuid): Article? {
        return get(id) {
            articleConverter.fromDynamoDB(it)
        }
    }
}
