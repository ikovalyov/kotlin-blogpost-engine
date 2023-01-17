package com.github.ikovalyov.model.service

import com.benasher44.uuid.Uuid
import com.github.ikovalyov.Api
import com.github.ikovalyov.model.security.User
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit

@OptIn(ExperimentalSerializationApi::class)
class UserService {
    suspend fun getAllUsers(): List<User> {
        val result = kotlin.runCatching {
            val result = window.fetch(
                Api.backendEndpoint + Api.userApiUrl,
                RequestInit(
                    credentials = RequestCredentials.INCLUDE
                )
            ).await().text().await()
            Json.decodeFromString(ListSerializer(User.serializer()), result)
        }
        if (result.isFailure) {
            println(result.exceptionOrNull())
        }
        return result.getOrNull() ?: emptyList()
    }

    suspend fun getUser(userId: Uuid): User {
        val result = window.fetch(
            Api.backendEndpoint + Api.userApiUrl + "/$userId",
            RequestInit(
                credentials = RequestCredentials.INCLUDE
            )
        ).await().text().await()
        return Json.decodeFromString(User.serializer(), result)
    }
}
