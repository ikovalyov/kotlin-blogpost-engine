package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.infrastructure.service.UserService
import com.github.ikovalyov.model.Article
import com.github.ikovalyov.model.extension.ArticleExtension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.ArticleExtension.toDynamoDbMap
import com.github.ikovalyov.model.extension.UserExtension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.UserExtension.toDynamoDbMap
import com.github.ikovalyov.model.extension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.toDynamoDbMap
import jakarta.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
@OptIn(ExperimentalSerializationApi::class)
class ArticleRepository(dynamoDbClient: DynamoDbAsyncClient) :
    CrudRepository<Article>(dynamoDbClient) {

    private lateinit var userService: UserService

    companion object {
        const val tableName = "article"
    }

    override val tableName = ArticleRepository.tableName

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun list(): List<Article> {
        val users = userService.getAllUsers()
        return list {
            Article.fromDynamoDbMap(it, users.associateBy { user ->
                user.id
            })
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
        val users = userService.getAllUsers()
        return get(id) {
            Article.fromDynamoDbMap(it, users.associateBy { user ->
                user.id
            })
        }
    }
}
