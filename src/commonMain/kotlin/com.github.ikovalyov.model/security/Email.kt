package com.github.ikovalyov.model.security

import com.github.ikovalyov.model.validator.EmailValidator
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlinx.serialization.Contextual
import kotlinx.serialization.Transient

@Serializable
@JvmInline
value class Email(val value: ShortString) {

    init {
        val validator = EmailValidator()
        if (!validator.validate(value)) {
            throw IllegalArgumentException("email is invalid")
        }
    }

    override fun toString() = value.toString()
}
