package com.github.ikovalyov

import com.github.ikovalyov.model.Person
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.views.View

@Controller("/views")
class ViewsController {
    @View("home")
    @Get("/pogo")
    fun pogo(): HttpResponse<Person> {
        return HttpResponse.ok(Person("sdelamo", true))
    }
}