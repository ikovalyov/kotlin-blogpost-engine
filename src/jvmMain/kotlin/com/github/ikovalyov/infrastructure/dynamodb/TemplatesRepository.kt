package com.github.ikovalyov.infrastructure.dynamodb

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.ikovalyov.model.Template
import io.micronaut.http.HttpStatus
import javax.inject.Singleton
import kotlinx.coroutines.future.await
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

@Singleton
class TemplatesRepository(
    private val dynamoDbClient: DynamoDbAsyncClient,
    private val objectMapper: ObjectMapper
) {
    companion object{
        const val tableName = "template"
    }

    suspend fun getViews(): List<Template> {
        val scanResponse = dynamoDbClient.scan {
            it.tableName(tableName)
        }.await()
        return scanResponse.items().map(Template::fromDynamoDbMap)
    }

    suspend fun insertView(template: Template): HttpStatus {
        val result = dynamoDbClient.putItem {
            it.tableName(tableName).item(
                objectMapper.convertValue(
                    template, object:TypeReference<Map<String, String>>() {}
                ).mapValues { entry ->
                    AttributeValue.builder().s(entry.value).build()
                }
            )
        }.await()
        return HttpStatus.valueOf(result.sdkHttpResponse().statusCode())
    }
}
