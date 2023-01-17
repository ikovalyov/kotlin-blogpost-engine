package com.github.ikovalyov.model.validator

import com.github.ikovalyov.model.security.ShortString

class EmailValidator: ValidatorInterface<ShortString> {
    private val emailAddressRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)".toRegex()

    override fun validate(input: ShortString): Boolean {
        return input.toString().matches(emailAddressRegex)
    }
}