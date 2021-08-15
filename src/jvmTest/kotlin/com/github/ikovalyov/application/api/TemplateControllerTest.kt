package com.github.ikovalyov.application.api

import com.github.ikovalyov.command.DynamoDbInitCommand
import com.github.ikovalyov.model.Template
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
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
      LocalStackContainer(localstackImage).withServices(LocalStackContainer.Service.DYNAMODB).also {
        it.start()
      }

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
  fun crud() = runBlocking {
    testList()
    testGet()
    testInsert()
    testUpdate()
    testDelete()
  }

  private suspend fun testDelete() {
    val resultString = controller.list()
    val result = Json.decodeFromString(ListSerializer(Template.serializer()), resultString)
    assert(result.count() == 2)
    controller.delete(result[0].id)
    val newResultString = controller.list()
    val newResult = Json.decodeFromString(ListSerializer(Template.serializer()), newResultString)
    assert(newResult.count() == 1)
  }

  private suspend fun testUpdate() {
    val templateString = controller.get("home.ftl")
    val template = Json.decodeFromString(Template.serializer(), templateString)
    val newTemplate = template.copy(template = template.template + "TEST")
    controller.update(newTemplate)
    val updatedTemplateString = controller.get("home.ftl")
    val updatedTemplate = Json.decodeFromString(Template.serializer(), updatedTemplateString)
    assert(updatedTemplate.template.endsWith("TEST"))
  }

  private suspend fun testInsert() {
    val templateString = controller.get("home.ftl")
    val template = Json.decodeFromString(Template.serializer(), templateString)
    val newTemplate = template.copy(id = "home2.ftl", lastModified = Clock.System.now())
    controller.insert(newTemplate)
    val newTemplateString = controller.get("home2.ftl")
    val newTemplateResponseBody = Json.decodeFromString(Template.serializer(), newTemplateString)
    assert(newTemplateResponseBody.id == "home2.ftl")
  }

  private suspend fun testGet() {
    val templateResponseString = controller.get("home.ftl")
    val templateResponse = Json.decodeFromString(Template.serializer(), templateResponseString)
    assert(templateResponse.id == "home.ftl")
  }

  private suspend fun testList() {
    val resultString = controller.list()
    val result = Json.decodeFromString(ListSerializer(Template.serializer()), resultString)
    assert(result.count() == 1)
  }
}
