package com.github.ikovalyov.model.security

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class ShortString(val value: String) {
    companion object {
        const val SHORT_STRING_SIZE = 256
    }
    init {
        if (value.length > SHORT_STRING_SIZE) {
            throw IllegalArgumentException("value can't be longer than $SHORT_STRING_SIZE characters")
        }
    }

    override fun toString() = value
}
