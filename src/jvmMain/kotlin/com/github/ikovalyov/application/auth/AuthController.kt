package com.github.ikovalyov.application.auth

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.UsersRepository
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
import kotlinx.serialization.ExperimentalSerializationApi
import java.net.URI

@Controller("/auth")
@OptIn(ExperimentalSerializationApi::class)
class AuthController {
    @Inject private lateinit var usersRepository: UsersRepository

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
        val user = usersRepository.getUserByEmail(userEmail)
        if (user == null) {
            usersRepository.insert(
                User(
                    id = Uuid.randomUUID(),
                    email = userEmail,
                    loggedIn = true,
                    nickname = "",
                    roles = emptyList(),
                    password = Password(ShortString("")),
                ),
            )
        }

        val location = URI(Api.FRONTEND_ENDPOINT)
        return HttpResponse.temporaryRedirect(location)
    }
}
