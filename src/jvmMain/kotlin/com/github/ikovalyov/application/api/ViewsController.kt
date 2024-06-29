package com.github.ikovalyov.application.api

import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplateRepository
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
class ViewsController(private val templateRepository: TemplateRepository) {
    @Get("/list-views")
    suspend fun listViews(): List<TemplateListItem> = templateRepository.list().map(TemplateListItem::fromTemplate)

    @Post("/add")
    suspend fun insertView(@Body template: Template): HttpResponse<*> {
        templateRepository.insert(template)
        return HttpResponse.accepted<Template>()
    }
}
