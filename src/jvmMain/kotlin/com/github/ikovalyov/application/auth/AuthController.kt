package com.github.ikovalyov.application.auth

import com.github.ikovalyov.Api
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import java.net.URI

@Controller("/auth")
class AuthController {
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/login")
    fun login(): HttpResponse<Any> {
        val location = URI(Api.frontendEndpoint)
        return HttpResponse.redirect(location)
    }
}
