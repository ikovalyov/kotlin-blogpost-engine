package com.github.ikovalyov.model.security

data class User(
    val id: Identifier,
    val email: Email,
    val loggedIn: Boolean,
    val nickname: String,
    val roles: List<Role>,
    val password: Password)
