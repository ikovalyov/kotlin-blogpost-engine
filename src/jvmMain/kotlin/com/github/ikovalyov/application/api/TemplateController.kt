package com.github.ikovalyov.application.api

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplatesRepository
import com.github.ikovalyov.model.Template
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

@Controller(Api.TEMPLATE_API_URL)
@Secured(SecurityRule.IS_AUTHENTICATED)
class TemplateController(private val templatesRepository: TemplatesRepository) {
    private val logger = KotlinLogging.logger {}

    @Get
    suspend fun list(): String = Json.encodeToString(ListSerializer(Template.serializer()), templatesRepository.list())

    @Get("/{itemId}")
    suspend fun get(itemId: Uuid): String? {
        val template = templatesRepository.get(itemId) ?: return null
        return Json.encodeToString(Template.serializer(), template)
    }

    @Delete("/{itemId}")
    suspend fun delete(itemId: Uuid): HttpResponse<Nothing> {
        val result = templatesRepository.delete(itemId)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Post
    suspend fun insert(@Body item: Template): HttpResponse<Nothing> {
        val result = templatesRepository.insert(item)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Patch
    suspend fun update(@Body item: Template): HttpResponse<Nothing> {
        val result = templatesRepository.update(item)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }
}
