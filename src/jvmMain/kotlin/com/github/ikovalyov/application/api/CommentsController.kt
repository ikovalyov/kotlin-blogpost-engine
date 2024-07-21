package com.github.ikovalyov.application.api

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.articles.CommentsRepository
import com.github.ikovalyov.model.Comment
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

@Controller(Api.COMMENTS_API_URL)
@Secured(SecurityRule.IS_AUTHENTICATED)
class CommentsController(private val commentsRepository: CommentsRepository) {
    @Get
    suspend fun list(): String = Json.encodeToString(ListSerializer(Comment.serializer()), commentsRepository.list())

    @Get("{id}")
    suspend fun get(id: Uuid): String? {
        val comment = commentsRepository.get(id) ?: return null
        return Json.encodeToString(Comment.serializer(), comment)
    }

    @Post("/")
    @Consumes(MediaType.APPLICATION_JSON)
    suspend fun insert(@Body comment: Comment): HttpResponse<Nothing> {
        val result = commentsRepository.insert(comment)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Delete("/{itemId}")
    suspend fun delete(itemId: Uuid): HttpResponse<Nothing> {
        val result = commentsRepository.delete(itemId)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }

    @Patch
    suspend fun update(@Body comment: Comment): HttpResponse<Nothing> {
        val result = commentsRepository.update(comment)
        return if (result) {
            HttpResponse.accepted()
        } else {
            HttpResponse.notFound()
        }
    }
}
