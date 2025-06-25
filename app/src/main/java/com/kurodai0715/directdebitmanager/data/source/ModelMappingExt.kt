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
    type = type,
)

fun Source.toLocalTransferItem() = LocalTransferItem(
    id = id,
    label = name,
    isSourceItem = true,
    type = type,
    parentId = null,
)
