package com.github.ikovalyov.application.api

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.UsersRepository
import com.github.ikovalyov.model.security.User
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Patch
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import mu.KotlinLogging

@ExperimentalSerializationApi
@Controller(Api.USERS_API_URL)
@Secured(SecurityRule.IS_AUTHENTICATED)
class UsersApiController(private val usersRepository: UsersRepository) {
    private val logger = KotlinLogging.logger {}

    @Get
    suspend fun list(): String = Json.encodeToString(ListSerializer(User.serializer()), usersRepository.list())

    @Get("/{itemId}")
    suspend fun get(itemId: Uuid): String? {
        val template = usersRepository.get(itemId) ?: return null
        return Json.encodeToString(User.serializer(), template)
    }

    @Delete("/{itemId}")
    suspend fun delete(itemId: Uuid): HttpResponse<Nothing> {
        val result = usersRepository.delete(itemId)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Post
    suspend fun insert(@Body item: String): HttpResponse<Nothing> {
        val user = Json.decodeFromString(User.serializer(), item)
        val result = usersRepository.insert(user)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Patch
    suspend fun update(@Body item: String): HttpResponse<Nothing> {
        val user = Json.decodeFromString(User.serializer(), item)
        val result = usersRepository.update(user)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }
}
