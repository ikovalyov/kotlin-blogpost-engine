package com.github.ikovalyov.application.api

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuid4
import com.github.ikovalyov.DockerImages
import com.github.ikovalyov.command.DynamoDbInitCommand
import com.github.ikovalyov.model.Article
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
class ArticlesControllerTest : TestPropertyProvider {
    @Inject lateinit var controller: ArticlesController

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
        val result = Json.decodeFromString(ListSerializer(Article.serializer()), resultString)
        assert(result.count() == 1)
        uuid = result.first().id
    }

    @Test
    @Order(2)
    fun testGet() = runBlocking {
        val articleResponseString = controller.get(uuid)
        require(articleResponseString != null) { "$uuid article not found" }
        val articleResponse = Json.decodeFromString(Article.serializer(), articleResponseString)
        assert(articleResponse.id == uuid)
    }

    @Test
    @Order(3)
    fun testInsert() = runBlocking {
        val articleString = controller.get(uuid)
        require(articleString != null) { "$uuid article not found" }
        val article = Json.decodeFromString(Article.serializer(), articleString)
        val newArticle = article.copy(id = uuid2, name = "new name")
        controller.insert(newArticle)
        val newArticleString = controller.get(uuid2)
        require(newArticleString != null) { "$uuid2 article not found" }
        val newArticleResponseBody = Json.decodeFromString(Article.serializer(), newArticleString)
        assert(newArticleResponseBody.id == uuid2)
    }

    @Test
    @Order(4)
    fun testUpdate() = runBlocking {
        val articleString = controller.get(uuid)
        require(articleString != null) { "$uuid article not found" }
        val article = Json.decodeFromString(Article.serializer(), articleString)
        val newArticle = article.copy(name = article.name + "TEST")
        controller.update(newArticle)
        val updatedArticleString = controller.get(uuid)
        require(updatedArticleString != null) { "$uuid article not found" }
        val updatedArticle = Json.decodeFromString(Article.serializer(), updatedArticleString)
        assert(updatedArticle.name.endsWith("TEST"))
    }

    @Test
    @Order(5)
    fun testDelete() = runBlocking {
        val resultString = controller.list()
        val result = Json.decodeFromString(ListSerializer(Article.serializer()), resultString)
        assert(result.count() == 2)
        controller.delete(result[0].id)
        val newResultString = controller.list()
        val newResult = Json.decodeFromString(ListSerializer(Article.serializer()), newResultString)
        assert(newResult.count() == 1)
    }
}
