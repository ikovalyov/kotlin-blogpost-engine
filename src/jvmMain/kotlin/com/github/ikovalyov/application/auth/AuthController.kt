package com.github.ikovalyov.application.auth

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.UserRepository
import com.github.ikovalyov.model.security.Email
import com.github.ikovalyov.model.security.Password
import com.github.ikovalyov.model.security.ShortString
import com.github.ikovalyov.model.security.User
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.context.ServerRequestContext
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import java.net.URI
import kotlinx.serialization.ExperimentalSerializationApi

@Controller("/auth")
@OptIn(ExperimentalSerializationApi::class)
class AuthController {
    @Inject private lateinit var userRepository: UserRepository

    @OptIn(ExperimentalSerializationApi::class)
    @Secured(SecurityRule.IS_AUTHENTICATED)
    @Get("/login")
    suspend fun login(): HttpResponse<Any> {

        val requestOpt = ServerRequestContext.currentRequest<Any>()
        if (requestOpt.isEmpty) {
            throw IllegalStateException("Request not found")
        }
        val request = requestOpt.get()
        val existingPrincipal = request.getUserPrincipal(Authentication::class.java)
        val userEmailString = existingPrincipal.get().attributes["email"].toString()
        val userEmail = Email(ShortString(userEmailString))
        val user = userRepository.getUserByEmail(userEmail)
        if (user == null) {
            userRepository.insert(User(
                id = Uuid.randomUUID(),
                email = userEmail,
                loggedIn = true,
                nickname = "",
                roles = emptyList(),
                password = Password(ShortString(""))
            ))
        }

        val location = URI(Api.frontendEndpoint)
        return HttpResponse.temporaryRedirect(location)
    }
}
