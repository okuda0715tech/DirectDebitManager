package com.kurodai0715.directdebitmanager.ui.screen.register_payee

import com.kurodai0715.directdebitmanager.domain.model.Payee2
import com.kurodai0715.directdebitmanager.domain.model.PayeeName

fun RegisterPayeeUiState.toDomain(): Payee2 {
    val name = PayeeName(payeeName)

    return when (val mode = editMode) {
        is PayeeEditMode.Add -> {
            Payee2.InMemory(
                name = name
            )
        }

        is PayeeEditMode.Edit -> {
            Payee2.Persisted(
                name = name,
                id = mode.id
            )
        }
    }
}
