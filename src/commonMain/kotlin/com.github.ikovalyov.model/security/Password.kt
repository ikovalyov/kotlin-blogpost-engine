package com.github.ikovalyov.model.security

import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

@Serializable
@JvmInline value class Password(val value: ShortString)
