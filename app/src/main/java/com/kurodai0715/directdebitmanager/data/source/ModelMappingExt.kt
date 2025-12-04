package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalDestWithSource
import com.kurodai0715.directdebitmanager.data.source.local.LocalDestination
import com.kurodai0715.directdebitmanager.data.source.local.LocalSource
import com.kurodai0715.directdebitmanager.data.source.local.LocalTransferItem
import com.kurodai0715.directdebitmanager.domain.TransferItemType
import com.kurodai0715.directdebitmanager.ui.screen.destination_edit.DestinationUiModel
import com.kurodai0715.directdebitmanager.ui.screen.destination_list.DestWithSourceUiModel

fun LocalDestination.toExternal() = DestinationUiModel(
    id = id,
    name = name,
    sourceId = sourceId,
//    date = date,
//    amount = amount.toString(),
)

fun DestinationUiModel.toLocal() = LocalDestination(
    id = id,
    name = name,
    sourceId = sourceId,
//    date = date,
//    amount = amount.toString(),
)

fun Source.toLocal() = LocalSource(
    id = id,
    name = name,
    type = type,
)

fun LocalDestWithSource.toExternal() = DestWithSourceUiModel(
    destId = destId,
    destName = destName,
    sourceId = sourceId,
    sourceName = sourceName,
)

fun LocalTransferItem.toSource(): Source {
    val type = requireNotNull(type) { "LocalTransferItem.type is null" }

    return Source(
        id = id,
        name = label,
        typeEnum = TransferItemType.fromInt(type),
        type = type,
    )
}

fun Source.toLocalTransferItem() = LocalTransferItem(
    id = id,
    label = name,
    isSourceItem = true,
    type = type,
    parentId = null,
)

fun DestinationUiModel.toLocalTransferItem() = LocalTransferItem(
    id = id,
    label = name,
    isSourceItem = false, // TODO [Destination] クラスに isSourceItem 項目を追加して、それを渡すように修正する
    type = null,
    parentId = sourceId,
)

fun LocalTransferItem.toExternal() = TransferItem(
    id = id,
    label = label,
    isSourceItem = isSourceItem,
    type = type,
    sourceId = parentId,
)
