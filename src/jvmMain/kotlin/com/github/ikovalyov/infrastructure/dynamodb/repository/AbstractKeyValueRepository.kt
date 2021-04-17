package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.github.ikovalyov.infrastructure.dynamodb.InitDynamoDbDatabaseInterface
import kotlinx.coroutines.future.await
import mu.KotlinLogging
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.DynamoDbResponse
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement
import software.amazon.awssdk.services.dynamodb.model.KeyType
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType
import software.amazon.awssdk.services.dynamodb.model.Tag

abstract class AbstractKeyValueRepository(protected val dynamoDbClient: DynamoDbAsyncClient) :
    InitDynamoDbDatabaseInterface {
    companion object {
        const val primaryKey = "id"
    }

    abstract val tableName: String
    private val logger = KotlinLogging.logger {}

    private val tableBuilder: CreateTableRequest.Builder by lazy {
        CreateTableRequest.builder().also {
            it.tableName(tableName)
            it.attributeDefinitions(
                AttributeDefinition.builder()
                    .attributeName(primaryKey)
                    .attributeType(ScalarAttributeType.S)
                    .build())
            it.keySchema(
                KeySchemaElement.builder().attributeName("id").keyType(KeyType.HASH).build())
            it.provisionedThroughput { builder ->
                builder.readCapacityUnits(5).writeCapacityUnits(5).build()
            }
            it.tags(listOf(Tag.builder().key("Owner").value("Ik").build()))
        }
    }

    override suspend fun init(): DynamoDbResponse? {
        logger.info { "creating $tableName table" }
        return try {
            dynamoDbClient.createTable(tableBuilder.build()).await()
        } catch (t: ResourceInUseException) {
            logger.warn(t) { "Table already exists" }
            null
        }
    }
}
