package com.github.ikovalyov.model.security.service

import com.github.ikovalyov.model.security.User

actual class SecurityService {
    actual suspend fun getCurrentUser(): User {
        TODO("Not yet implemented")
    }
}