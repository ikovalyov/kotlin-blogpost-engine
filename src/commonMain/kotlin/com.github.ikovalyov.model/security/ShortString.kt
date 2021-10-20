package com.github.ikovalyov.model.security

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class ShortString(val value: String) {
    companion object {
        const val shortStringSize = 256
    }
    init {
        if (value.length > shortStringSize) {
            throw IllegalArgumentException("value can't be longer than $shortStringSize characters")
        }
    }

    override fun toString() = value.toString()
}
