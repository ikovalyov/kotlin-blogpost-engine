package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ikovalyov.model.Template
import io.micronaut.http.HttpStatus
import javax.inject.Singleton
import kotlinx.coroutines.future.await
import mu.KotlinLogging
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Singleton
class TemplateRepository(
    dynamoDbClient: DynamoDbAsyncClient, private val objectMapper: ObjectMapper
) : AbstractKeyValueRepository(dynamoDbClient) {
    companion object {
        const val tableName = "template"
    }

    private val logger = KotlinLogging.logger {}

    override val tableName = TemplateRepository.tableName

    suspend fun list(): List<Template> {
        val scanResponse = dynamoDbClient.scan { it.tableName(tableName) }.await()
        return scanResponse.items().map(Template::fromDynamoDbMap)
    }

    suspend fun insert(template: Template): Boolean {
        return dynamoDbClient
            .putItem {
                it.tableName(tableName)
                    .item(
                        objectMapper.convertValue(
                                template, object : TypeReference<Map<String, String>>() {})
                            .mapValues { entry -> AttributeValue.builder().s(entry.value).build() })
            }
            .await()
            .sdkHttpResponse()
            .statusCode() == HttpStatus.ACCEPTED.code
    }

    suspend fun update(template: Template) {
        try {
            delete(template.id)
            insert(template)
        } catch (t: Throwable) {
            logger.error(t) { t.message }
            insert(template)
        }
    }

    suspend fun get(templateName: String): Template? {
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

    suspend fun delete(templateName: String): Boolean {
        val response =
            dynamoDbClient
                .deleteItem {
                    it.tableName(tableName)
                        .key(mapOf(primaryKey to AttributeValue.builder().s(templateName).build()))
                }
                .await()
        return response.sdkHttpResponse().isSuccessful
    }

    override suspend fun init(): Boolean {
        super.init()
        val stream = javaClass.classLoader.getResourceAsStream("template/home.ftl")!!
        val templateString = stream.readAllBytes().decodeToString()
        val template = Template.create("home.ftl", templateString)
        return insert(template)
    }
}
