package com.github.ikovalyov.application.api

import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplateRepository
import com.github.ikovalyov.model.Template
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Patch
import io.micronaut.http.annotation.Post
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import mu.KotlinLogging

@Controller(Api.templateUrl)
class TemplateController(private val templateRepository: TemplateRepository) {
    private val logger = KotlinLogging.logger {}

    @Get
    suspend fun list(): String {
        return Json.encodeToString(ListSerializer(Template.serializer()), templateRepository.list())
    }

    @Get("/{itemId}")
    suspend fun get(itemId: String): String {
        return Json.encodeToString(Template.serializer(), templateRepository.get(itemId)!!)
    }

    @Delete("/{itemId}")
    suspend fun delete(itemId: String): HttpResponse<Nothing> {
        val result = templateRepository.delete(itemId)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Post
    suspend fun insert(@Body item: Template): HttpResponse<Nothing> {
        val result = templateRepository.insert(item)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Patch
    suspend fun update(@Body item: Template): HttpResponse<Nothing> {
        val result = templateRepository.update(item)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }
}
