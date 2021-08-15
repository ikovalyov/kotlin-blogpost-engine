package com.github.ikovalyov.infrastructure.dynamodb.repository

import io.micronaut.context.annotation.Property
import javax.inject.Singleton
import kotlinx.coroutines.future.await
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse

@Singleton
class ConfigurationRepository(
    dynamoDbClient: DynamoDbAsyncClient,
    @Property(name = "blog.template.default-template") private val defaultTemplate: String
) : AbstractKeyValueRepository(dynamoDbClient) {
  companion object {
    const val tableName = "configuration"
    const val activeTemplateKeyId = "active-template"
    const val configurationValueFieldName = "value"
  }
  override val tableName = ConfigurationRepository.tableName

  override suspend fun init(): Boolean {
    super.init()
    return insertConfiguration(activeTemplateKeyId, defaultTemplate)
  }

  suspend fun getActiveTemplateName(): String? {
    return getConfigProperty(activeTemplateKeyId).item()[configurationValueFieldName]?.s()
  }

  private suspend fun insertConfiguration(key: String, value: String): Boolean {
    return dynamoDbClient
        .putItem {
          it.tableName(tableName)
              .item(
                  mapOf(
                      primaryKey to AttributeValue.builder().s(key).build(),
                      configurationValueFieldName to AttributeValue.builder().s(value).build()))
        }
        .await()
        .sdkHttpResponse()
        .isSuccessful
  }

  private suspend fun getConfigProperty(key: String): GetItemResponse {
    return dynamoDbClient
        .getItem {
          it.tableName(tableName)
          it.key(mapOf(primaryKey to AttributeValue.builder().s(key).build()))
        }
        .await()
  }
}
