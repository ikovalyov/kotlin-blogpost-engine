package com.github.ikovalyov

import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplateRepository
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.TemplateListItem
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.views.View

@Controller("/views")
class ViewsController(private val templateRepository: TemplateRepository) {
    @Get("/list-views")
    suspend fun listViews(): List<TemplateListItem> {
        return templateRepository.list().map(TemplateListItem::fromTemplate)
    }

    @Post("/add")
    suspend fun insertView(@Body template: Template): HttpResponse<*> {
        templateRepository.insert(template)
        return HttpResponse.accepted<Template>()
    }
}
