package com.github.ikovalyov.application.api

import com.github.ikovalyov.command.DynamoDbInitCommand
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.testcontainers.containers.localstack.LocalStackContainer
import org.testcontainers.utility.DockerImageName

@MicronautTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TemplateControllerTest : TestPropertyProvider {
    @Inject lateinit var controller: TemplateController
    @Inject lateinit var dynamoDbInitCommand: DynamoDbInitCommand
    private val localstackImage = DockerImageName.parse("localstack/localstack:0.11.3")

    private val localstack: LocalStackContainer =
        LocalStackContainer(localstackImage)
            .withServices(LocalStackContainer.Service.DYNAMODB)
            .also { it.start() }

    override fun getProperties(): MutableMap<String, String> {
        return mutableMapOf(
            "micronaut.server.port" to "0",
            "blog.aws.endpoint" to
                localstack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString())
    }

    @BeforeAll
    fun init() {
        dynamoDbInitCommand.run()
    }

    @Test
    fun crud() =
        runBlocking {
            testList()
            testGet()
            testInsert()
            testUpdate()
            testDelete()
        }

    private suspend fun testDelete() {
        val result = controller.list()
        assert(result.count() == 2)
        controller.delete(result[0].id)
        assert(controller.list().count() == 1)
    }

    private suspend fun testUpdate() {
        val template = controller.get("home.ftl").body()!!
        val newTemplate = template.copy(template = template.template + "TEST")
        controller.update(newTemplate)
        val updatedTemplate = controller.get("home.ftl").body()!!
        assert(updatedTemplate.template.endsWith("TEST"))
    }

    private suspend fun testInsert() {
        val template = controller.get("home.ftl").body()!!
        val newTemplate = template.copy(id = "home2.ftl", lastModified = Clock.System.now())
        controller.insert(newTemplate)
        val newTemplateResponseBody = controller.get("home2.ftl").body()
        assert(newTemplateResponseBody?.id == "home2.ftl")
    }

    private suspend fun testGet() {
        val templateResponse = controller.get("home.ftl")
        assert(templateResponse.body()!!.id == "home.ftl")
    }

    private suspend fun testList() {
        val result = controller.list()
        assert(result.count() == 1)
    }
}
