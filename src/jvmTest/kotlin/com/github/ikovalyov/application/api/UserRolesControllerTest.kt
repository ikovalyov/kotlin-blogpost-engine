package com.github.ikovalyov.application.api

import com.benasher44.uuid.uuid4
import com.github.ikovalyov.command.DynamoDbInitCommand
import com.github.ikovalyov.model.security.UserRole
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.test.support.TestPropertyProvider
import jakarta.inject.Inject
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
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
import kotlin.test.assertEquals
import kotlin.test.assertNull

@MicronautTest
@TestMethodOrder(OrderAnnotation::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class UserRolesControllerTest : TestPropertyProvider {
    companion object {
        private val uuid = uuid4()
        private val uuid2 = uuid4()
        private val name = "name"
        private val name2 = "name2"
    }
    @Inject lateinit var userRolesController: UserRolesController
    @Inject lateinit var dynamoDbInitCommand: DynamoDbInitCommand
    private val localstackImage = DockerImageName.parse("localstack/localstack:0.11.3")

    private val localstack: LocalStackContainer =
        LocalStackContainer(localstackImage).withServices(LocalStackContainer.Service.DYNAMODB).also {
            it.start()
        }

    override fun getProperties(): MutableMap<String, String> {
        return mutableMapOf(
            "micronaut.server.port" to "0",
            "blog.aws.endpoint" to localstack.getEndpointOverride(LocalStackContainer.Service.DYNAMODB).toString()
        )
    }

    @BeforeAll
    fun init() {
        dynamoDbInitCommand.run()
    }

    @Test
    @Order(1)
    fun insert(): Unit = runBlocking {
        with(userRolesController.insert(UserRole(uuid, Clock.System.now(), name))) {
            assert(this.status == HttpStatus.ACCEPTED)
        }
        with(userRolesController.insert(UserRole(uuid2, Clock.System.now(), name2))) {
            assert(this.status == HttpStatus.ACCEPTED)
        }
    }

    @Test
    @Order(2)
    fun list() = runBlocking {
        val listJson = userRolesController.list()
        val result = Json.decodeFromString(ListSerializer(UserRole.serializer()), listJson)
        assert(result.count() == 2)
    }

    @Test
    @Order(3)
    fun getItems() = runBlocking {
        with(userRolesController.get(UserRolesControllerTest.uuid)!!) {
            with(Json.decodeFromString(UserRole.serializer(), this)) {
                assertEquals(UserRolesControllerTest.name, body)
            }
        }
        with(userRolesController.get(UserRolesControllerTest.uuid2)!!) {
            with(Json.decodeFromString(UserRole.serializer(), this)) {
                assertEquals(UserRolesControllerTest.name2, body)
            }
        }
    }

    @Test
    @Order(4)
    fun delete() = runBlocking {
        with(userRolesController.delete(uuid2)) {
            assertEquals(HttpStatus.ACCEPTED, status)
        }
        val item = userRolesController.get(uuid2)
        assertNull(item)
    }

    @Test
    @Order(5)
    fun update() = runBlocking {
        val userRole = UserRole(uuid2, Clock.System.now(), UserRolesControllerTest.name)
        userRolesController.update(userRole)

        with(userRolesController.get(UserRolesControllerTest.uuid2)!!) {
            with(Json.decodeFromString(UserRole.serializer(), this)) {
                assertEquals(UserRolesControllerTest.name, name)
            }
        }
    }
}
