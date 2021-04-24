package com.github.ikovalyov.application.api

import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplateRepository
import com.github.ikovalyov.model.Template
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Patch
import io.micronaut.http.annotation.Post

@Controller("/api/controller/template")
class TemplateController(private val templateRepository: TemplateRepository) {
    @Get
    suspend fun list(): List<Template> {
        return templateRepository.list()
    }

    @Get("/{itemId}")
    suspend fun get(itemId: String): HttpResponse<Template> {
        return templateRepository.get(itemId)?.let { HttpResponse.ok(it) }
            ?: HttpResponse.notFound()
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
    suspend fun insert(@Body item: Template) {
        templateRepository.insert(item)
    }

    @Patch
    suspend fun update(@Body item: Template) {
        templateRepository.update(item)
    }
}
