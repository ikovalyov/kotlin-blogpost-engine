package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.github.ikovalyov.infrastructure.dynamodb.InitDynamoDbDatabaseInterface
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
class RecordsRepository(dynamoDbClient: DynamoDbAsyncClient) :
    AbstractKeyValueRepository(dynamoDbClient),
    InitDynamoDbDatabaseInterface {
    companion object {
        const val TABLE_NAME = "record"
    }
    override val tableName = TABLE_NAME
}
