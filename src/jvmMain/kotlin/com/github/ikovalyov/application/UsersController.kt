package com.github.ikovalyov.application

import com.github.ikovalyov.Api
import com.github.ikovalyov.model.security.Security
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.context.ServerRequestContext
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule

@Controller(Api.userUrl)
@Secured(SecurityRule.IS_ANONYMOUS)
class UsersController {

    @Get("/")
    fun userInfo() : Security {
        val requestOpt = ServerRequestContext.currentRequest<Any>()
        if (requestOpt.isEmpty) {
            throw IllegalStateException("Request not found")
        }
        val request = requestOpt.get()
        val existingPrincipal = request.getUserPrincipal(Authentication::class.java)
        return if (existingPrincipal.isPresent) {
            Security(
                attributes = existingPrincipal.get().attributes
            )
        } else {
            Security(attributes = emptyMap())
        }
    }
}
