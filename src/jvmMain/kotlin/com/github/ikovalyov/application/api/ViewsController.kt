package com.github.ikovalyov.application.api

import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplatesRepository
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.TemplateListItem
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule

@Controller(Api.VIEWS_API_URL)
@Secured(SecurityRule.IS_AUTHENTICATED)
class ViewsController(private val templatesRepository: TemplatesRepository) {
    @Get("/list-views")
    suspend fun listViews(): List<TemplateListItem> = templatesRepository.list().map(TemplateListItem::fromTemplate)

    @Post("/add")
    suspend fun insertView(@Body template: Template): HttpResponse<*> {
        templatesRepository.insert(template)
        return HttpResponse.accepted<Template>()
    }
}
