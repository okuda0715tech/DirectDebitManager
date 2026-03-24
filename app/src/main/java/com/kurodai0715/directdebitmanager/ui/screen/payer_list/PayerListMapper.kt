package com.kurodai0715.directdebitmanager.ui.screen.payer_list

import com.kurodai0715.directdebitmanager.data.source.local.PaymentItemEntity
import com.kurodai0715.directdebitmanager.domain.model.ItemType
import com.kurodai0715.directdebitmanager.domain.model.SourceUiModel

private fun PaymentItemEntity.toSourceUiModel(): SourceUiModel {
    val type = requireNotNull(typeCode) { "type is null" }

    return SourceUiModel(
        id = id,
        name = label,
        type = ItemType.fromInt(type),
    )
}

fun List<PaymentItemEntity>.toSourceUiModels(): List<SourceUiModel> {
    return map { it.toSourceUiModel() }
}