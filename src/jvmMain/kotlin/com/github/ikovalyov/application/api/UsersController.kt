package com.github.ikovalyov.application.api

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.UserRepository
import com.github.ikovalyov.infrastructure.dynamodb.repository.UserRoleRepository
import com.github.ikovalyov.model.security.User
import com.github.ikovalyov.model.security.UserRole
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Patch
import io.micronaut.http.annotation.Post
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import mu.KotlinLogging

@ExperimentalSerializationApi
@Controller(Api.userUrl)
class UsersController(private val userRepository: UserRepository) {
    private val logger = KotlinLogging.logger {}

    @Get
    suspend fun list(): String {
        return Json.encodeToString(ListSerializer(User.serializer()), userRepository.list())
    }

    @Get("/{itemId}")
    suspend fun get(itemId: Uuid): String? {
        val template = userRepository.get(itemId) ?: return null
        return Json.encodeToString(User.serializer(), template)
    }

    @Delete("/{itemId}")
    suspend fun delete(itemId: Uuid): HttpResponse<Nothing> {
        val result = userRepository.delete(itemId)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Post
    suspend fun insert(@Body item: String): HttpResponse<Nothing> {
        val user = Json.decodeFromString(User.serializer(), item)
        val result = userRepository.insert(user)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Patch
    suspend fun update(@Body item: String): HttpResponse<Nothing> {
        val user = Json.decodeFromString(User.serializer(), item)
        val result = userRepository.update(user)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }
}
