package com.github.ikovalyov.application.api

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.github.ikovalyov.DockerImages
import com.github.ikovalyov.command.DynamoDbInitCommand
import com.github.ikovalyov.model.Tag
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation::class)
class TagControllerTest : TestPropertyProvider {
    @Inject lateinit var controller: TagsController

    @Inject lateinit var dynamoDbInitCommand: DynamoDbInitCommand
    private val localstackImage = DockerImageName.parse(DockerImages.LOCALSTACK_IMAGE_NAME)

    private val localstack: LocalStackContainer =
        LocalStackContainer(localstackImage).withServices(LocalStackContainer.Service.DYNAMODB).also {
            it.start()
        }

    override fun getProperties(): MutableMap<String, String> = mutableMapOf(
        "micronaut.server.port" to "0",
        "blog.aws.endpoint" to localstack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString(),
    )

    private lateinit var uuid: Uuid
    private val uuid2 = uuid4()

    @BeforeAll
    fun init() {
        dynamoDbInitCommand.run()
    }

    @Test
    @Order(1)
    fun testList() = runBlocking {
        val resultString = controller.list()
        val result = Json.decodeFromString(ListSerializer(Tag.serializer()), resultString)
        assert(result.count() == 3)
        uuid = result.first().id
    }

    @Test
    @Order(2)
    fun testGet() = runBlocking {
    val tagResponseString = controller.get(uuid)
        require(tagResponseString != null) { "$uuid tag not found" }
        val templateResponse = Json.decodeFromString(Tag.serializer(), tagResponseString)
        assert(templateResponse.id == uuid)
    }

    @Test
    @Order(3)
    fun testInsert() = runBlocking {
    val tagString = controller.get(uuid)
        require(tagString != null) { "$uuid tag not found" }
        val template = Json.decodeFromString(Tag.serializer(), tagString)
        val newTag = template.copy(id = uuid2, name = "new name")
        controller.insert(newTag)
        val newTagString = controller.get(uuid2)
        require(newTagString != null) { "$uuid2 tag not found" }
        val newTagResponseBody = Json.decodeFromString(Tag.serializer(), newTagString)
        assert(newTagResponseBody.id == uuid2)
    }

    @Test
    @Order(4)
    fun testUpdate() = runBlocking {
    val tagString = controller.get(uuid)
        require(tagString != null) { "$uuid tag not found" }
        val tag = Json.decodeFromString(Tag.serializer(), tagString)
        val newTag = tag.copy(name = tag.name + "TEST")
        controller.update(newTag)
        val updatedTagString = controller.get(uuid)
        require(updatedTagString != null) { "$uuid tag not found" }
        val updatedTemplate = Json.decodeFromString(Tag.serializer(), updatedTagString)
        assert(updatedTemplate.name.endsWith("TEST"))
    }

    @Test
    @Order(5)
    fun testDelete() = runBlocking {
    val resultString = controller.list()
        val result = Json.decodeFromString(ListSerializer(Tag.serializer()), resultString)
        assert(result.count() == 4)
        controller.delete(result[0].id)
        val newResultString = controller.list()
        val newResult = Json.decodeFromString(ListSerializer(Tag.serializer()), newResultString)
        assert(newResult.count() == 3)
    }
}
