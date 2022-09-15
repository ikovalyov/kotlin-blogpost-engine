package com.github.ikovalyov.model.security

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
value class Email(val value: ShortString) {
    override fun toString() = value.toString()
}
