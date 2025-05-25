package com.kurodai0715.directdebitmanager.ui.domain

interface Validator {
    fun validate(input: String): Boolean
}

object EmptyValidator : Validator {
    override fun validate(input: String): Boolean {
        return input.isNotEmpty()
    }
}

sealed class EmptyValidationResult {
    object Empty : EmptyValidationResult()
    object Valid : EmptyValidationResult()
}

object EmailValidator : Validator {
    override fun validate(input: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(input).matches()
    }
}

object PasswordValidator : Validator {
    override fun validate(input: String): Boolean {
        return input.length >= 8
    }
}

// TODO 複数のバリデーターを組み合わせた場合に、成功か失敗しか返せず、失敗した原因が返せないため、返せるように要修正。
/**
 * 複数のバリデーターを組み合わせた例.
 */
object IdValidator : Validator {

    val validators = listOf(EmailValidator, PasswordValidator)

    override fun validate(input: String): Boolean {
        return validators.all { it.validate(input) }
    }
}

