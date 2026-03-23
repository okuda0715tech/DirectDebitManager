package com.kurodai0715.directdebitmanager.ui.screen.payee_list

import com.kurodai0715.directdebitmanager.domain.model.Payee2

fun Payee2.Persisted.toPayeeUiModel(): PayeeUiModel {
    return PayeeUiModel(
        id = id,
        name = name.value,
    )
}

fun List<Payee2.Persisted>.toPayeeUiModels(): List<PayeeUiModel>{
    return map { it.toPayeeUiModel() }
}
