package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2

fun PaymentEntityV2.toTransferRelationListItem(): TransferRelationListUiState.Success.Item {
    return TransferRelationListUiState.Success.Item(
        id = id,
        name = label,
        payerId = parentId,
    )
}

fun List<PaymentEntityV2>.toTransferRelationList(): List<TransferRelationListUiState.Success.Item> {
    return map { it.toTransferRelationListItem() }
}