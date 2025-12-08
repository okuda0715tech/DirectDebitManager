package com.kurodai0715.directdebitmanager.domain


/**
 * すべての Validator の基底となるインターフェース.
 */
interface Validator {
    fun validate(input: String): ValidationResult
}

/**
 * すべてのバリデーション結果を同一視するための型.
 */
sealed interface ValidationResult {
    /**
     * バリデーションに成功した場合.
     */
    object Valid : ValidationResult

    /**
     * [EmptyValidator] に失敗した場合.
     */
    object EmptyError : ValidationResult

    /**
     * [LengthWithin100Validator] に失敗した場合.
     */
    object LengthWithin100Error : ValidationResult
}

/**
 * 入力が空文字の場合に [ValidationResult.EmptyError] を返す [Validator].
 */
object EmptyValidator : Validator {
    override fun validate(input: String): ValidationResult {
        return when (input.isEmpty()) {
            true -> ValidationResult.EmptyError
            false -> ValidationResult.Valid
        }
    }
}

/**
 * 入力の文字数が 0 ～ 100 文字以外の場合に [ValidationResult.LengthWithin100Error] を返す Validator.
 */
object LengthWithin100Validator : Validator {
    override fun validate(input: String): ValidationResult {
        return when (input.length) {
            in 0..100 -> ValidationResult.Valid
            else -> ValidationResult.LengthWithin100Error
        }
    }
}

/**
 * 複数の Validator を組み合わせて、連続して実行するためのクラス.
 */
class CompositeValidator(vararg validators: Validator) : Validator {

    private val validators: List<Validator> = validators.toList()

    override fun validate(input: String): ValidationResult {
        for (validator in validators) {
            val result = validator.validate(input)
            if (result != ValidationResult.Valid) return result
        }
        return ValidationResult.Valid
    }
}

/**
 * [EmptyValidator] と [LengthWithin100Validator] を組み合わせて使用する Validator.
 */
val BasicTextValidator = CompositeValidator(EmptyValidator, LengthWithin100Validator)
