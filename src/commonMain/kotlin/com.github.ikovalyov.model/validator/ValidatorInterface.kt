package com.github.ikovalyov.model.validator

interface ValidatorInterface<T> {
    fun validate(input: T): Boolean
}