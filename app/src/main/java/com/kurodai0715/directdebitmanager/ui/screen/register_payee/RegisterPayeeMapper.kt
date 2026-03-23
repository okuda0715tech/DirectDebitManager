package com.kurodai0715.directdebitmanager.ui.screen.register_payee

import com.kurodai0715.directdebitmanager.domain.model.Payee2
import com.kurodai0715.directdebitmanager.domain.model.PayeeName

fun RegisterPayeeUiState.toDomain(): Payee2 {
    val name = PayeeName(payeeName)

    return when (id) {
        // TODO 条件判定を型で表現する。
        0 -> {
            Payee2.InMemory(
                name = name
            )
        }

        else -> {
            Payee2.Persisted(
                name = name,
                id = id
            )
        }
    }
}
