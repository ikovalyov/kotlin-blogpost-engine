package com.github.ikovalyov.infrastructure.dynamodb

interface InitDynamoDbDatabaseInterface {
  suspend fun init(): Boolean?
}
