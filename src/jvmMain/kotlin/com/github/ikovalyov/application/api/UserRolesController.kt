package com.github.ikovalyov.application.api

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.UserRoleRepository
import com.github.ikovalyov.model.security.UserRole
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Patch
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import mu.KotlinLogging

@Controller(Api.userRoleApiUrl)
@Secured(SecurityRule.IS_AUTHENTICATED)
class UserRolesController(private val userRolesRepository: UserRoleRepository) {
    private val logger = KotlinLogging.logger {}

    @Get
    suspend fun list(): String {
        return Json.encodeToString(ListSerializer(UserRole.serializer()), userRolesRepository.list())
    }

    @Get("/{itemId}")
    suspend fun get(itemId: Uuid): String? {
        val template = userRolesRepository.get(itemId) ?: return null
        return Json.encodeToString(UserRole.serializer(), template)
    }

    @Delete("/{itemId}")
    suspend fun delete(itemId: Uuid): HttpResponse<Nothing> {
        val result = userRolesRepository.delete(itemId)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Post
    suspend fun insert(@Body item: String): HttpResponse<Nothing> {
        val userRole = Json.decodeFromString(UserRole.serializer(), item)
        val result = userRolesRepository.insert(userRole)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Patch
    suspend fun update(@Body item: String): HttpResponse<Nothing> {
        val userRole = Json.decodeFromString(UserRole.serializer(), item)
        val result = userRolesRepository.update(userRole)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }
}
