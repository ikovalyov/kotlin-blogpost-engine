package com.github.ikovalyov.model.service

import com.github.ikovalyov.Api
import com.github.ikovalyov.model.Template
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import org.w3c.fetch.INCLUDE
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit

class TemplateService {
    private val logger = KotlinLogging.logger {}
    suspend fun getAllTemplates(): List<Template> {
        val result = kotlin.runCatching {
            val result = window.fetch(
                Api.BACKEND_ENDPOINT + Api.TEMPLATE_API_URL,
                RequestInit(
                    credentials = RequestCredentials.INCLUDE,
                ),
            ).await().text().await()
            Json.decodeFromString(ListSerializer(Template.serializer()), result)
        }

        if (result.isFailure) {
            logger.error(result.exceptionOrNull()) {
                "Error while getting list of templates"
            }
        }
        return result.getOrNull() ?: emptyList()
    }
}
