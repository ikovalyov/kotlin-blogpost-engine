package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.github.ikovalyov.model.Tag
import com.github.ikovalyov.model.extension.TagExtension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.TagExtension.toDynamoDbMap
import jakarta.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
class TagsRepository(dynamoDbClient: DynamoDbAsyncClient) : CrudRepository<Tag>(dynamoDbClient) {
    companion object {
        const val TABLE_NAME = "tag"
    }

    override val tableName = TABLE_NAME

    override suspend fun init(): Boolean {
        super.init()
        val articleTag = Tag(
            id = uuid4(),
            name = "article",
        )
        val noteTag = Tag(
            id = uuid4(),
            name = "note",
        )
        val quoteTag = Tag(
            id = uuid4(),
            name = "quote",
        )
        return coroutineScope {
            val jobs = listOf(
                async {
                    insert(articleTag)
                },
                async {
                    insert(noteTag)
                },
                async {
                    insert(quoteTag)
                },
            )
            return@coroutineScope jobs.awaitAll().all { it }
        }
    }

    suspend fun list(): List<Tag> = list {
        Tag.fromDynamoDbMap(it)
    }

    suspend fun insert(item: Tag): Boolean = insert(item) {
        it.toDynamoDbMap()
    }

    suspend fun update(item: Tag): Boolean = update(item) {
        it.toDynamoDbMap()
    }

    suspend fun get(id: Uuid): Tag? = get(id) {
        Tag.fromDynamoDbMap(it)
    }
}
