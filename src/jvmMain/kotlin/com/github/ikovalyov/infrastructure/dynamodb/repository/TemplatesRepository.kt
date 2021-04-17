package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ikovalyov.model.Template
import javax.inject.Singleton
import kotlinx.coroutines.future.await
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.DynamoDbResponse
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse

@Singleton
class TemplatesRepository(
    dynamoDbClient: DynamoDbAsyncClient, private val objectMapper: ObjectMapper
) : AbstractKeyValueRepository(dynamoDbClient) {
    companion object {
        const val tableName = "template"
    }

    override val tableName = TemplatesRepository.tableName

    suspend fun getViews(): List<Template> {
        val scanResponse = dynamoDbClient.scan { it.tableName(tableName) }.await()
        return scanResponse.items().map(Template::fromDynamoDbMap)
    }

    suspend fun insertTemplate(template: Template): PutItemResponse {
        return dynamoDbClient
            .putItem {
                it.tableName(tableName)
                    .item(
                        objectMapper.convertValue(
                                template, object : TypeReference<Map<String, String>>() {})
                            .mapValues { entry -> AttributeValue.builder().s(entry.value).build() })
            }
            .await()
    }

    suspend fun getTemplate(templateName: String): Template? {
        val response =
            dynamoDbClient
                .getItem {
                    it.tableName(tableName)
                    it.key(mapOf(primaryKey to AttributeValue.builder().s(templateName).build()))
                }
                .await()
        if (!response.hasItem()) return null
        return Template.fromDynamoDbMap(response.item())
    }

    override suspend fun init(): DynamoDbResponse {
        super.init()
        val stream = javaClass.classLoader.getResourceAsStream("template/home.ftl")!!
        val templateString = stream.readAllBytes().decodeToString()
        val template = Template.create("home.ftl", templateString)
        return insertTemplate(template)
    }
}
