package com.github.ikovalyov.model.security.service

import com.github.ikovalyov.Api
import com.github.ikovalyov.model.Template
import com.github.ikovalyov.model.security.User
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit

actual class SecurityService {
    @OptIn(ExperimentalSerializationApi::class)
    actual suspend fun getCurrentUser(): User {
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
        return Json.decodeFromString(User.serializer(), result)
    }
}