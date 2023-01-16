package com.github.ikovalyov.model.extension

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.model.Article
import com.github.ikovalyov.model.security.User
import kotlinx.serialization.ExperimentalSerializationApi
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

object ArticleExtension {
    fun Article.toDynamoDbMap(): Map<String, AttributeValue> {
        val mutableMap = mutableMapOf<String, AttributeValue>(
            "id" to AttributeValue.builder().s(id.toString()).build(),
            "name" to AttributeValue.builder().s(name.toString()).build(),
            "abstract" to AttributeValue.builder().s(abstract).build(),
            "body" to AttributeValue.builder().s(body).build(),
        )
        if (meta !== null && meta.isNotEmpty()) {
            mutableMap["meta"] = AttributeValue.builder().ss(meta).build()
        }
        if (tags !== null && tags.isNotEmpty()) {
            mutableMap["tags"] = AttributeValue.builder().ss(tags).build()
        }
        if (author !== null) {
            mutableMap["author"] = AttributeValue.builder().s(author.toString()).build()
        }
        if (template !== null) {
            mutableMap["template"] = AttributeValue.builder().s(template.toString()).build()
        }
        return mutableMap
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun Article.Companion.fromDynamoDbMap(
        map: Map<String, AttributeValue>,
        usersList: Map<Uuid, User>
    ): Article {
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
            author = usersList[author]!!,
            tags = map["tags"]?.ss(),
            meta = map["meta"]?.ss(),
            template = template
        ) {
            usersList[it]!!
        }
    }
}
