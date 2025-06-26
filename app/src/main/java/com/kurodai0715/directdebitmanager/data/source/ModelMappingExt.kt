package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalDestWithSource
import com.kurodai0715.directdebitmanager.data.source.local.LocalDestination
import com.kurodai0715.directdebitmanager.data.source.local.LocalSource
import com.kurodai0715.directdebitmanager.data.source.local.LocalTransferItem

fun LocalDestination.toExternal() = Destination(
    id = id,
    name = name,
    sourceId = sourceId,
//    date = date,
//    amount = amount.toString(),
)

fun Destination.toLocal() = LocalDestination(
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

fun LocalDestWithSource.toExternal() = DestWithSource(
    destId = destId,
    destName = destName,
    sourceId = sourceId,
    sourceName = sourceName,
)

fun LocalTransferItem.toSource() = Source(
    id = id,
    name = label,
    type = type!!,
)

fun Source.toLocalTransferItem() = LocalTransferItem(
    id = id,
    label = name,
    isSourceItem = true,
    type = type,
    parentId = null,
)

fun Destination.toLocalTransferItem() = LocalTransferItem(
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
