package com.github.ikovalyov.model.security

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline value class Email(val value: ShortString) {
    override fun toString() = value.toString()
}
