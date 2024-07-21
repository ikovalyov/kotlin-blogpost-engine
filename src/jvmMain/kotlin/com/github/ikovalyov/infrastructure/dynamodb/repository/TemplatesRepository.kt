package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.github.ikovalyov.infrastructure.dynamodb.InitDynamoDbDatabaseInterface
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.extension.TemplateExtension.fromDynamoDbMap
import com.github.ikovalyov.model.extension.TemplateExtension.toDynamoDbMap
import jakarta.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@Singleton
class TemplatesRepository(dynamoDbClient: DynamoDbAsyncClient) :
    CrudRepository<Template>(dynamoDbClient),
    InitDynamoDbDatabaseInterface {
    companion object {
        const val TABLE_NAME = "template"
    }

    override val tableName = TABLE_NAME

    suspend fun list(): List<Template> = list {
        Template.fromDynamoDbMap(it)
    }

    suspend fun insert(item: Template): Boolean = insert(item) {
        it.toDynamoDbMap()
    }

    suspend fun update(item: Template): Boolean = update(item) {
        it.toDynamoDbMap()
    }

    suspend fun get(id: Uuid): Template? = get(id) {
        Template.fromDynamoDbMap(it)
    }

    override suspend fun init(): Boolean {
        super.init()
        val stream = javaClass.classLoader.getResourceAsStream("template/home.ftl")!!
        val templateString = withContext(Dispatchers.IO) {
            stream.readAllBytes()
        }.decodeToString()
        val template = Template.create(uuid4(), "home.ftl", templateString)
        return insert(template)
    }
}
