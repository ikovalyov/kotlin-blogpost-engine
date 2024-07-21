package com.github.ikovalyov.infrastructure.service

import com.github.ikovalyov.infrastructure.dynamodb.repository.UsersRepository
import com.github.ikovalyov.model.security.User
import jakarta.inject.Inject
import jakarta.inject.Singleton

@Singleton
class UserService {
    @Inject private lateinit var usersRepository: UsersRepository

    suspend fun getAllUsers(): List<User> = usersRepository.list()
}
