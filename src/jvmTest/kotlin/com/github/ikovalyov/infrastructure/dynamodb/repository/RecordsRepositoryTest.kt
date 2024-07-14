package com.github.ikovalyov.infrastructure.dynamodb.repository

import com.github.ikovalyov.LocalstackTestImages
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import kotlinx.coroutines.future.await
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle
import org.testcontainers.containers.localstack.LocalStackContainer
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient

@MicronautTest
@TestInstance(Lifecycle.PER_CLASS)
internal class RecordsRepositoryTest : TestPropertyProvider {
    var localstack: LocalStackContainer = LocalStackContainer(LocalstackTestImages.LOCALSTACK_2_3_IMAGE)
        .withServices(
            LocalStackContainer.Service.DYNAMODB,
        )

    @Inject lateinit var client: DynamoDbAsyncClient

    @Inject lateinit var recordsRepository: RecordsRepository

    init {
        localstack.start()
    }

    @Test
    fun testTableWasCreated() = runBlocking {
        recordsRepository.init()
        val response = client.describeTable { it.tableName(recordsRepository.tableName) }.await()
        assert(response.sdkHttpResponse().statusCode() == 200)
        assert(response.table().tableName() == recordsRepository.tableName)
    }

    override fun getProperties(): MutableMap<String, String> = mutableMapOf("blog.aws.endpoint" to localstack.endpoint.toString())
}
