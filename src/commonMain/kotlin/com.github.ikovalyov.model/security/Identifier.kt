package com.github.ikovalyov.model.security

import kotlin.jvm.JvmInline

class Identifier(value: String) {
    companion object{
        const val identifierSize = 36
    }
    init {
        if (value.length > identifierSize) {
            throw IllegalArgumentException("value can't be longer than $identifierSize characters")
        }
    }

    val content: ByteArray = value.encodeToByteArray().take(identifierSize).toByteArray()
}