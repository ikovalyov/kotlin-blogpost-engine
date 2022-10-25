package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.Article
import com.github.ikovalyov.model.extension.ArticleExtension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.ArticleExtension.toDynamoDbMap
import com.github.ikovalyov.model.extension.UserExtension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.UserExtension.toDynamoDbMap
import com.github.ikovalyov.model.extension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.toDynamoDbMap
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
class ArticleRepository(dynamoDbClient: DynamoDbAsyncClient) :
    CrudRepository<Article>(dynamoDbClient) {
    companion object {
        const val tableName = "article"
    }

    override val tableName = ArticleRepository.tableName

    suspend fun list(): List<Article> {
        return list {
            Article.fromDynamoDbMap(it)
        }
    }

    suspend fun insert(item: Article): Boolean {
        return insert(item) {
            item.toDynamoDbMap()
        }
    }

    suspend fun update(item: Article): Boolean {
        return update(item) {
            item.toDynamoDbMap()
        }
    }

    suspend fun get(id: Uuid): Article? {
        return get(id) {
            Article.fromDynamoDbMap(it)
        }
    }
}
