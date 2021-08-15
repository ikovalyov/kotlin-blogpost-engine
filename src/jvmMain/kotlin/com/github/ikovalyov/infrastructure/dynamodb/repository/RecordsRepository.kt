package com.github.ikovalyov.infrastructure.dynamodb.repository

import javax.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
class RecordsRepository(dynamoDbClient: DynamoDbAsyncClient) :
    AbstractKeyValueRepository(dynamoDbClient) {
  companion object {
    const val tableName = "record"
  }
  override val tableName = RecordsRepository.tableName
}
