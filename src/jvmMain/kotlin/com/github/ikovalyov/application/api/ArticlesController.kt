package com.github.ikovalyov.application.api

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.ArticlesRepository
import com.github.ikovalyov.model.Article
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Consumes
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Patch
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

@Controller(Api.ARTICLES_API_URL)
@Secured(SecurityRule.IS_AUTHENTICATED)
class ArticlesController(private val articlesRepository: ArticlesRepository) {
    @Get
    suspend fun list(): String = Json.encodeToString(ListSerializer(Article.serializer()), articlesRepository.list())

    @Get("{id}")
    suspend fun get(id: Uuid): String? {
        val article = articlesRepository.get(id) ?: return null
        return Json.encodeToString(Article.serializer(), article)
    }

    @Post("/")
    @Consumes(MediaType.APPLICATION_JSON)
    suspend fun insert(@Body item: Article): HttpResponse<Nothing> {
        val result = articlesRepository.insert(item)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Delete("/{itemId}")
    suspend fun delete(itemId: Uuid): HttpResponse<Nothing> {
        val result = articlesRepository.delete(itemId)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Patch
    suspend fun update(@Body item: Article): HttpResponse<Nothing> {
        val result = articlesRepository.update(item)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }
}
