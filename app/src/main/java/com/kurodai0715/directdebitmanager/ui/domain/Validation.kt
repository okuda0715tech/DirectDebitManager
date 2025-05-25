package com.kurodai0715.directdebitmanager.ui.domain

sealed class ValidationState{
    object Pending: ValidationState()
    object InProgress: ValidationState()
    object Success: ValidationState()
    object Failure: ValidationState()
}

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

object IdValidator : Validator {

    val validators = listOf(EmptyValidator, EmailValidator)

    override fun validate(input: String): Boolean {
        return validators.all { it.validate(input) }
    }
}

object ValidationUtil {

    fun validateEmpty(input: String): EmptyValidationResult {
        if (!EmptyValidator.validate(input)) {
            return EmptyValidationResult.Empty
        }
        return EmptyValidationResult.Valid
    }

}
