package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.extension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.toDynamoDbMap
import jakarta.inject.Singleton
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
class TemplateRepository(dynamoDbClient: DynamoDbAsyncClient) :
    CrudRepository<Template>(dynamoDbClient) {
    companion object {
        const val tableName = "template"
    }


    override val tableName = TemplateRepository.tableName

    suspend fun list(): List<Template> {
        return list {
            Template.fromDynamoDbMap(it)
        }
    }

    suspend fun insert(item: Template): Boolean {
        return insert(item) {
            it.toDynamoDbMap()
        }
    }

    suspend fun update(item: Template): Boolean {
        return update(item) {
            it.toDynamoDbMap()
        }
    }

    suspend fun get(id: Uuid): Template? {
        return get(id) {
            Template.fromDynamoDbMap(it)
        }
    }

    override suspend fun init(): Boolean {
        super.init()
        val stream = javaClass.classLoader.getResourceAsStream("template/home.ftl")!!
        val templateString = stream.readAllBytes().decodeToString()
        val template = Template.create(uuid4(), "home.ftl", templateString)
        return insert(template)
    }
}
