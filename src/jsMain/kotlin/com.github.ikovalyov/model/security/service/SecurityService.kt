package com.github.ikovalyov.model.security.service

import com.github.ikovalyov.Api
import com.github.ikovalyov.model.security.User
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit

actual class SecurityService {
    private val logger = KotlinLogging.logger {}

    @OptIn(ExperimentalSerializationApi::class)
    actual suspend fun getCurrentUser(): User? {
        val user = kotlin.runCatching {
            val result = window
                .fetch(
                    Api.backendEndpoint + Api.userUrl,
                    RequestInit(
                        credentials = RequestCredentials.INCLUDE
                    )
                )
                .await()
                .text()
                .await()
            Json.decodeFromString(User.serializer(), result)
        }
        if (user.isFailure) {
            logger.error(user.exceptionOrNull()) {
                "Error while getting list of users"
            }
        }
        return user.getOrNull()
    }
}
