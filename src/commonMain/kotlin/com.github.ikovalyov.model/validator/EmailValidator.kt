package com.github.ikovalyov.model.validator

import com.github.ikovalyov.model.security.ShortString

class EmailValidator : ValidatorInterface<ShortString> {
    private val emailAddressRegex = "^[A-Za-z](.*)(@)(.+)(\\.)(.+)".toRegex()

    override fun validate(input: ShortString): Boolean = input.toString().matches(emailAddressRegex)
}
