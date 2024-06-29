package com.github.ikovalyov.infrastructure.dynamodb.repository

import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
class RecordsRepository(dynamoDbClient: DynamoDbAsyncClient) : AbstractKeyValueRepository(dynamoDbClient) {
    companion object {
        const val TABLE_NAME = "record"
    }
    override val tableName = TABLE_NAME
}
