package com.kurodai0715.directdebitmanager.ui.domain

object ValidationUtil {

    fun validateEmpty(input: String): EmptyValidationResult {
        if (!EmptyValidator.validate(input)) {
            return EmptyValidationResult.Empty
        }
        return EmptyValidationResult.Valid
    }

}