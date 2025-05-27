package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalDestWithSource
import com.kurodai0715.directdebitmanager.data.source.local.LocalDestination
import com.kurodai0715.directdebitmanager.data.source.local.LocalSource

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

fun LocalSource.toExternal() = Source(
    id = id,
    name = name,
    type = type,
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
