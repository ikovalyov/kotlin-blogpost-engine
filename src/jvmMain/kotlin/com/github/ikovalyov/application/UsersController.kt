package com.github.ikovalyov.application

import com.github.ikovalyov.Api
import com.github.ikovalyov.infrastructure.dynamodb.repository.UsersRepository
import com.github.ikovalyov.model.security.Email
import com.github.ikovalyov.model.security.ShortString
import com.github.ikovalyov.model.security.User
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.context.ServerRequestContext
import io.micronaut.security.annotation.Secured
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.rules.SecurityRule
import jakarta.inject.Inject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@Controller(Api.USER_URL)
@Secured(SecurityRule.IS_ANONYMOUS)
@OptIn(ExperimentalSerializationApi::class)
class UsersController {
    @Inject
    private lateinit var usersRepository: UsersRepository

    @Get("/")
    suspend fun userInfo(): String? {
        val requestOpt = ServerRequestContext.currentRequest<Any>()
        if (requestOpt.isEmpty) {
            throw IllegalStateException("Request not found")
        }
        val request = requestOpt.get()
        val existingPrincipal = request.getUserPrincipal(Authentication::class.java)
        return if (existingPrincipal.isPresent) {
            val userEmailString = existingPrincipal.get().attributes["email"].toString()
            val userEmail = Email(ShortString(userEmailString))
            val user = usersRepository.getUserByEmail(userEmail)
            if (user == null) {
                null
            } else {
                Json.encodeToString(User.serializer(), user)
            }
        } else {
            null
        }
    }
}
