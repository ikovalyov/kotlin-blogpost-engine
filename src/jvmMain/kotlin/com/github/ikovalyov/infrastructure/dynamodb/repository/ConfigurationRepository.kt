package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import io.micronaut.context.annotation.Property
import jakarta.inject.Singleton
import kotlinx.coroutines.future.await
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse

@Singleton
class ConfigurationRepository(dynamoDbClient: DynamoDbAsyncClient, @Property(name = "blog.template.default-template") private val defaultTemplate: String) : AbstractKeyValueRepository(dynamoDbClient) {
    companion object {
        const val TABLE_NAME = "configuration"
        const val ACTIVE_TEMPLATE_KEY_ID = "active-template"
        const val CONFIGURATION_VALUE_FIELD_NAME = "value"
    }
    override val tableName = TABLE_NAME

    override suspend fun init(): Boolean {
        super.init()
        return insertConfiguration(ACTIVE_TEMPLATE_KEY_ID, defaultTemplate)
    }

    suspend fun getActiveTemplateId(): Uuid? = Uuid.fromString(
        getConfigProperty(ACTIVE_TEMPLATE_KEY_ID).item()[CONFIGURATION_VALUE_FIELD_NAME]?.s(),
    )

    private suspend fun insertConfiguration(key: String, value: String): Boolean = dynamoDbClient
        .putItem {
            it.tableName(tableName)
                .item(
                    mapOf(
                        PRIMARY_KEY to AttributeValue.builder().s(key).build(),
                        CONFIGURATION_VALUE_FIELD_NAME to AttributeValue.builder().s(value).build(),
                    ),
                )
        }
        .await()
        .sdkHttpResponse()
        .isSuccessful

    private suspend fun getConfigProperty(key: String): GetItemResponse = dynamoDbClient
        .getItem {
            it.tableName(tableName)
            it.key(mapOf(PRIMARY_KEY to AttributeValue.builder().s(key).build()))
        }
        .await()
}
