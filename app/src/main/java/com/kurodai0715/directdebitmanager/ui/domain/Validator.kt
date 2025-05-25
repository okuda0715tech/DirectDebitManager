package com.kurodai0715.directdebitmanager.ui.domain

interface Validator {
    fun validate(input: String): ValidationResult
}

object EmptyValidator : Validator {
    override fun validate(input: String): ValidationResult {
        return when (input.isEmpty()) {
            true -> ValidationResult.EMPTY_ERROR
            false -> ValidationResult.VALID
        }
    }
}

object LengthWithin30Validator : Validator {
    override fun validate(input: String): ValidationResult {
        return when (input.length) {
            in 0..30 -> ValidationResult.VALID
            else -> ValidationResult.LENGTH_WITHIN_30_ERROR
        }
    }
}

enum class ValidationResult {
    EMPTY_ERROR,
    LENGTH_WITHIN_30_ERROR,
    VALID,
}

class CompositeValidator(vararg validators: Validator) : Validator {

    private val validators: List<Validator> = validators.toList()

    override fun validate(input: String): ValidationResult {
        for (validator in validators) {
            val result = validator.validate(input)
            if (result != ValidationResult.VALID) return result
        }
        return ValidationResult.VALID
    }
}

val BasicTextValidator = CompositeValidator(EmptyValidator, LengthWithin30Validator)
