package com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list

import com.kurodai0715.directdebitmanager.data.source.local.PaymentEntityV2

fun PaymentEntityV2.toTransferRelationListItem(): ScreenState.Success.Item {
    return ScreenState.Success.Item(
        name = label,
    )
}

fun List<PaymentEntityV2>.toTransferRelationList(): List<ScreenState.Success.Item> {
    return map { it.toTransferRelationListItem() }
}