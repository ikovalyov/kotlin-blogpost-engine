package com.github.ikovalyov.model.service

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api
import com.github.ikovalyov.model.security.User
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit

class UserService {
    private val logger = KotlinLogging.logger {}

    suspend fun getAllUsers(): List<User> {
        val result = kotlin.runCatching {
            val result = window.fetch(
                Api.BACKEND_ENDPOINT + Api.USER_API_URL,
                RequestInit(
                    credentials = RequestCredentials.INCLUDE,
                ),
            ).await().text().await()
            Json.decodeFromString(ListSerializer(User.serializer()), result)
        }

        if (result.isFailure) {
            logger.error(result.exceptionOrNull()) {
                "Error while getting list of users"
            }
        }
        return result.getOrNull() ?: emptyList()
    }

    suspend fun getUser(userId: Uuid): User {
        val result = window.fetch(
            Api.BACKEND_ENDPOINT + Api.USER_API_URL + "/$userId",
            RequestInit(
                credentials = RequestCredentials.INCLUDE,
            ),
        ).await().text().await()
        return Json.decodeFromString(User.serializer(), result)
    }
}
