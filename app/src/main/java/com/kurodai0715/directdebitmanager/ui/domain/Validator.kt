package com.kurodai0715.directdebitmanager.ui.domain

interface Validator {
    fun validate(input: String): ValidationResultInterface
}

interface ValidationResultInterface

object Valid : ValidationResultInterface

object EmptyValidator : Validator {
    override fun validate(input: String): ValidationResultInterface {
        return when (input.isEmpty()) {
            true -> EmptyError
            false -> Valid
        }
    }
}

object EmptyError : ValidationResultInterface

object LengthWithin30Validator : Validator {
    override fun validate(input: String): ValidationResultInterface {
        return when (input.length) {
            in 0..30 -> Valid
            else -> LengthWithin30Error
        }
    }
}

object LengthWithin30Error : ValidationResultInterface

class CompositeValidator(vararg validators: Validator) : Validator {

    private val validators: List<Validator> = validators.toList()

    override fun validate(input: String): ValidationResultInterface {
        for (validator in validators) {
            val result = validator.validate(input)
            if (result != Valid) return result
        }
        return Valid
    }
}

val BasicTextValidator = CompositeValidator(EmptyValidator, LengthWithin30Validator)
