package com.github.ikovalyov.template

import com.github.ikovalyov.model.Template
import freemarker.cache.TemplateLoader
import java.io.Reader
import java.io.StringReader
import javax.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest

@Singleton
class DynamoDbTemplateLoader(private val client: DynamoDbAsyncClient): TemplateLoader {
    companion object{
        const val tableName = "template"
    }
    /**
     * awslocal dynamodb create-table --table-name template --attribute-definitions \
     * AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH \
     * --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 \
     * --tags Key=Owner,Value=ik
     */
    override fun findTemplateSource(name: String): Template? {
        val keyToGet = mapOf(
            "id" to AttributeValue.builder().s(name).build()
        )

        val request = GetItemRequest.builder()
            .key(keyToGet)
            .tableName(tableName)
            .build()
        val response = client.getItem(request).get()
        return if (response.hasItem()) {
            val item = response.item()
            Template.fromDynamoDbMap(item)
        } else {
            null
        }
    }

    override fun getLastModified(templateSource: Any?): Long {
        require(templateSource is Template)
        return templateSource.lastModified.epochSecond
    }

    override fun getReader(templateSource: Any?, encoding: String?): Reader {
        require(templateSource is Template)
        return StringReader(templateSource.template)
    }

    override fun closeTemplateSource(templateSource: Any?) { }
}
