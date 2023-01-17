package com.github.ikovalyov.infrastructure.dynamodb.converter

import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.infrastructure.dynamodb.DynamodbConverterInterface
import com.github.ikovalyov.infrastructure.service.TemplateService
import com.github.ikovalyov.infrastructure.service.UserService
import com.github.ikovalyov.model.Article
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.serialization.ExperimentalSerializationApi
import software.amazon.awssdk.services.dynamodb.model.AttributeValue


@OptIn(ExperimentalSerializationApi::class)
@Singleton
class DynamodbArticleConverter: DynamodbConverterInterface<Article> {
    @Inject lateinit var userService: UserService
    @Inject lateinit var templateService: TemplateService

    override suspend fun fromDynamoDB(map: Map<String, AttributeValue>): Article {
        val users = userService.getAllUsers()
        val templates = templateService.getAllTemplates()

        val tags = map["tags"]?.ss()
        val author = map["author"]?.let {
            uuidFrom(it.s())
        }
        val template = map["template"]?.let {
            uuidFrom(it.s())
        }
        return Article(
            id = uuidFrom(map["id"]!!.s()),
            name = map["name"]!!.s(),
            abstract = map["abstract"]!!.s(),
            body = map["body"]!!.s(),
            author = users.first {
                it.id.toString() == author.toString()
            },
            tags = map["tags"]?.ss(),
            meta = map["meta"]?.ss(),
            template = templates.first {
                it.id.toString() == template.toString()
            },
            userList = users,
            templateList = templates
        )
    }

    override suspend fun toDynamoDB(item: Article): Map<String, AttributeValue> {
        val mutableMap = mutableMapOf<String, AttributeValue>(
            "id" to AttributeValue.builder().s(item.id.toString()).build(),
            "name" to AttributeValue.builder().s(item.name).build(),
            "abstract" to AttributeValue.builder().s(item.abstract).build(),
            "body" to AttributeValue.builder().s(item.body).build(),
        )
        if (item.meta !== null && item.meta.isNotEmpty()) {
            mutableMap["meta"] = AttributeValue.builder().ss(item.meta).build()
        }
        if (item.tags !== null && item.tags.isNotEmpty()) {
            mutableMap["tags"] = AttributeValue.builder().ss(item.tags).build()
        }
        mutableMap["author"] = AttributeValue.builder().s(item.author.id.toString()).build()
        if (item.template !== null) {
            mutableMap["template"] = AttributeValue.builder().s(item.template.toString()).build()
        }
        return mutableMap
    }
}