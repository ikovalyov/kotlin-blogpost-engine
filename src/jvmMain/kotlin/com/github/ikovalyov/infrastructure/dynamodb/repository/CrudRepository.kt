package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.markers.IEditable
import io.micronaut.http.HttpStatus
import kotlinx.coroutines.future.await
import mu.KotlinLogging
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

abstract class CrudRepository<T : IEditable>(dynamoDbClient: DynamoDbAsyncClient) :
    AbstractKeyValueRepository(dynamoDbClient) {

    private val logger = KotlinLogging.logger {}

    protected suspend fun list(convert: (Map<String, AttributeValue>) -> T): List<T> {
        val scanResponse = dynamoDbClient.scan { it.tableName(tableName) }.await()
        return scanResponse.items().map(convert)
    }

    protected suspend fun insert(item: T, convert: (T) -> Map<String, AttributeValue>): Boolean {
        return dynamoDbClient
            .putItem { it.tableName(tableName).item(convert(item)) }
            .await()
            .sdkHttpResponse()
            .statusCode() == HttpStatus.OK.code
    }

    protected suspend fun update(item: T, convert: (T) -> Map<String, AttributeValue>): Boolean {
        return try {
            delete(item.id)
            insert(item, convert)
        } catch (t: Throwable) {
            logger.error(t) { t.message }
            insert(item, convert)
        }
    }

    protected suspend fun get(id: Uuid, convert: (Map<String, AttributeValue>) -> T): T? {
        val response = dynamoDbClient.getItem {
            it.tableName(tableName)
            it.key(mapOf(primaryKey to AttributeValue.builder().s(id.toString()).build()))
        }
            .await()
        if (!response.hasItem()) return null
        return convert(response.item())
    }

    suspend fun delete(id: Uuid): Boolean {
        val response = dynamoDbClient.deleteItem {
            it.tableName(tableName)
                .key(mapOf(primaryKey to AttributeValue.builder().s(id.toString()).build()))
        }
            .await()
        return response.sdkHttpResponse().isSuccessful
    }
}
