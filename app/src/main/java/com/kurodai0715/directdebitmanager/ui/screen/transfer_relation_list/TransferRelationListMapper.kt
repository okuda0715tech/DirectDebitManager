package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2

fun PaymentEntityV2.toTransferRelationListItem(): TransferRelationListUiState.Item {
    return TransferRelationListUiState.Item(
        name = label,
    )
}

fun List<PaymentEntityV2>.toTransferRelationList(): List<TransferRelationListUiState.Item> {
    return map { it.toTransferRelationListItem() }
}