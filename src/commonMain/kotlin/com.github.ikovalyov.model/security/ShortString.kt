package com.github.ikovalyov.model.security

class ShortString(value: String) {
  companion object {
    const val shortStringSize = 256
  }
  init {
    if (value.length > shortStringSize) {
      throw IllegalArgumentException("value can't be longer than $shortStringSize characters")
    }
  }
  val content: ByteArray = value.encodeToByteArray().take(shortStringSize).toByteArray()
}
