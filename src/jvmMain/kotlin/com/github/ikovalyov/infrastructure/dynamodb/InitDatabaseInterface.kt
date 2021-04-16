package com.github.ikovalyov.infrastructure.dynamodb

import software.amazon.awssdk.services.dynamodb.model.DynamoDbResponse

interface InitDynamoDbDatabaseInterface {
    suspend fun init(): DynamoDbResponse?
}
