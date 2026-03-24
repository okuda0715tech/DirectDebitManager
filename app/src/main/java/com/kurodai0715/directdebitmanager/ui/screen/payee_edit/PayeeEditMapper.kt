package com.kurodai0715.directdebitmanager.ui.screen.payee_edit

import com.kurodai0715.directdebitmanager.domain.model.Payee
import com.kurodai0715.directdebitmanager.domain.model.PayeeName

fun PayeeEditUiState.toDomain(): Payee {
    val name = PayeeName(payeeName)

    return when (val mode = editMode) {
        is PayeeEditMode.Add -> {
            Payee.InMemory(
                name = name
            )
        }

        is PayeeEditMode.Edit -> {
            Payee.Persisted(
                name = name,
                id = mode.id
            )
        }
    }
}
