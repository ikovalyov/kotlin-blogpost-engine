package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.model.markers.IEditable
import io.micronaut.http.HttpStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import mu.KotlinLogging
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

abstract class CrudRepository<T : IEditable>(dynamoDbClient: DynamoDbAsyncClient) : AbstractKeyValueRepository(dynamoDbClient) {

    private val logger = KotlinLogging.logger {}

    protected suspend fun list(convert: suspend (Map<String, AttributeValue>) -> T): List<T> = coroutineScope {
        val scanResponse = dynamoDbClient.scan { it.tableName(tableName) }.await()
        scanResponse.items().map {
            async {
                convert(it)
            }
        }.map {
            it.await()
        }
    }

    protected suspend fun insert(item: T, convert: suspend (T) -> Map<String, AttributeValue>): Boolean {
        val convertedItem = convert(item)
        return dynamoDbClient
            .putItem { it.tableName(tableName).item(convertedItem) }
            .await()
            .sdkHttpResponse()
            .statusCode() == HttpStatus.OK.code
    }

    protected suspend fun update(item: T, convert: suspend (T) -> Map<String, AttributeValue>): Boolean = try {
        delete(item.id)
        insert(item, convert)
    } catch (t: Throwable) {
        logger.error(t) { t.message }
        insert(item, convert)
    }

    protected suspend fun get(id: Uuid, convert: suspend (Map<String, AttributeValue>) -> T): T? {
        val response = dynamoDbClient.getItem {
            it.tableName(tableName)
            it.key(mapOf(PRIMARY_KEY to AttributeValue.builder().s(id.toString()).build()))
        }
            .await()
        if (!response.hasItem()) return null
        return convert(response.item())
    }

    suspend fun delete(id: Uuid): Boolean {
        val response = dynamoDbClient.deleteItem {
            it.tableName(tableName)
                .key(mapOf(PRIMARY_KEY to AttributeValue.builder().s(id.toString()).build()))
        }
            .await()
        return response.sdkHttpResponse().isSuccessful
    }

    protected suspend fun find(expressionAttributeValues: Map<String, AttributeValue>, filterExpression: String, convert: (Map<String, AttributeValue>) -> T): List<T> {
        val response = dynamoDbClient.scan {
            it.tableName(tableName)
                .expressionAttributeValues(expressionAttributeValues)
                .filterExpression(filterExpression)
        }.await()
        return response.items().map(convert)
    }
}
