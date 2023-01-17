package com.github.ikovalyov.infrastructure.dynamodb

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

interface DynamodbConverterInterface<T: Any> {
    suspend fun fromDynamoDB(map: Map<String, AttributeValue>): T
    suspend fun toDynamoDB(item: T): Map<String, AttributeValue>
}