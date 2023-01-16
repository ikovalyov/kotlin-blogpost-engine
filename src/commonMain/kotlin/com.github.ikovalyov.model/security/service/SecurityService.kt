package com.github.ikovalyov.model.security.service

import com.github.ikovalyov.model.security.User
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
expect class SecurityService {
    suspend fun getCurrentUser() : User
}