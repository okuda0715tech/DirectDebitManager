package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalTransferItem
import com.kurodai0715.directdebitmanager.domain.TransferItemType

fun LocalTransferItem.toSource(): Source {
    val type = requireNotNull(type) { "LocalTransferItem.type is null" }

    return Source(
        id = id,
        name = label,
        typeEnum = TransferItemType.fromInt(type),
        type = type,
    )
}

fun LocalTransferItem.toExternal() = TransferItem(
    id = id,
    label = label,
    isSourceItem = isSourceItem,
    type = type,
    sourceId = parentId,
)
