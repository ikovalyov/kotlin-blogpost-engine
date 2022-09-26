package com.github.ikovalyov.application.auth

import com.github.ikovalyov.Api
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.cookie.Cookie
import io.micronaut.http.netty.cookies.NettyCookie
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import java.net.URI
import mu.KLogger
import mu.KotlinLogging
import org.slf4j.LoggerFactory

@Controller("/auth")
class AuthController {
    private val logger = KotlinLogging.logger{ }
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/login")
    fun login(request: HttpRequest<Any>): HttpResponse<Any> {
        val location = URI(Api.frontendEndpoint)
        val response = HttpResponse.redirect<Any>(location)
        logger.debug {
            "redirecting to $location"
        }
        return response
    }
}