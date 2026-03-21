package com.kurodai0715.directdebitmanager.ui.screen.payee_list

import com.kurodai0715.directdebitmanager.domain.model.Payee

fun Payee.toPayeeUiModel(): PayeeUiModel {
    return PayeeUiModel(
        id = id,
        name = name.value,
    )
}

fun List<Payee>.toPayeeUiModels(): List<PayeeUiModel>{
    return map { it.toPayeeUiModel() }
}
