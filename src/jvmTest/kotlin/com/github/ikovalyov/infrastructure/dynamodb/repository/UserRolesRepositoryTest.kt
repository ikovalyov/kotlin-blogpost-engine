package com.github.ikovalyov.infrastructure.dynamodb.repository

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.testcontainers.containers.GenericContainer
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@MicronautTest
@TestInstance(Lifecycle.PER_CLASS)
internal class UserRolesRepositoryTest : TestPropertyProvider {
    class MyGenericContainer(dockerImageName: String) :
        GenericContainer<MyGenericContainer>(dockerImageName)

    private val dynamodbContainer =
        MyGenericContainer("amazon/dynamodb-local:1.13.2")
            .withCommand("-jar DynamoDBLocal.jar -inMemory -sharedDb")
            .withExposedPorts(8000)

    @Inject lateinit var client: DynamoDbAsyncClient

    @Inject lateinit var userRolesRepository: UserRoleRepository

    init {
        dynamodbContainer.start()
    }

    @Test
    fun testTableWasCreated() = runBlocking {
        userRolesRepository.init()
        val response = client.describeTable { it.tableName(userRolesRepository.tableName) }.await()
        assert(response.sdkHttpResponse().statusCode() == 200)
        assert(response.table().tableName() == userRolesRepository.tableName)
    }

    override fun getProperties(): MutableMap<String, String> {
        val endpointUrl = String.format("http://localhost:%d", dynamodbContainer.firstMappedPort)
        return mutableMapOf("blog.aws.endpoint" to endpointUrl)
    }
}
