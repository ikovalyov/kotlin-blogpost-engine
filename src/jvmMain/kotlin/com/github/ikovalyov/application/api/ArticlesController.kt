package com.github.ikovalyov.application.api

import com.benasher44.uuid.Uuid
import com.benasher44.uuid.uuidFrom
import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.ArticleRepository
import com.github.ikovalyov.model.Article
import com.github.ikovalyov.model.Item
import com.github.ikovalyov.model.security.User
import com.github.ikovalyov.model.security.UserRole
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
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
import kotlinx.datetime.Clock
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest

@Controller(Api.articleApiUrl)
@Secured(SecurityRule.IS_AUTHENTICATED)
class ArticlesController(private val articleRepository: ArticleRepository) {
    @Get
    suspend fun list(): String {
        return Json.encodeToString(ListSerializer(Article.serializer()), articleRepository.list())
    }

    @Get("{id}")
    suspend fun get(id: Uuid): String? {
        val article = articleRepository.get(id) ?: return null
        return Json.encodeToString(Article.serializer(), article)
    }

    @Post("/")
    @Consumes(MediaType.APPLICATION_JSON)
    suspend fun insert(@Body item: String): HttpResponse<Nothing> {
        val article = Json.decodeFromString(Article.serializer(), item)
        val result = articleRepository.insert(article)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Delete("/{itemId}")
    suspend fun delete(itemId: Uuid): HttpResponse<Nothing> {
        val result = articleRepository.delete(itemId)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Patch
    suspend fun update(@Body item: String): HttpResponse<Nothing> {
        val article = Json.decodeFromString(Article.serializer(), item)
        val result = articleRepository.update(article)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }
}
