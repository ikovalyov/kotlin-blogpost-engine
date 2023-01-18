package com.github.ikovalyov.model.security.service

import com.github.ikovalyov.model.security.User
import kotlinx.serialization.ExperimentalSerializationApi

actual class SecurityService {
    @OptIn(ExperimentalSerializationApi::class)
    actual suspend fun getCurrentUser(): User? {
        TODO("Not yet implemented")
    }
}
