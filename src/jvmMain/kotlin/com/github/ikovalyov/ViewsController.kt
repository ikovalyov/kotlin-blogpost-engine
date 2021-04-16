package com.github.ikovalyov

import com.github.ikovalyov.infrastructure.dynamodb.repository.TemplatesRepository
import com.github.ikovalyov.model.Person
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.TemplateListItem
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.views.View

@Controller("/views")
class ViewsController(
    private val templatesRepository: TemplatesRepository
) {
    @View("home")
    @Get("/pogo")
    fun pogo(): HttpResponse<Person> {
        return HttpResponse.ok(Person("sdelamo", true))
    }

    @Get("/list-views")
    suspend fun listViews(): List<TemplateListItem> {
        return templatesRepository.getViews().map(TemplateListItem::fromTemplate)
    }

    @Post("/add")
    suspend fun insertView(@Body template: Template): HttpResponse<*> {
        return HttpResponse.status<Any>(
            HttpStatus.valueOf(templatesRepository.insertTemplate(template).sdkHttpResponse().statusCode())
        )
    }
}
