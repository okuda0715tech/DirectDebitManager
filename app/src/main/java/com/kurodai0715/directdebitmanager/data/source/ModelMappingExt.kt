package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalDestination
import com.kurodai0715.directdebitmanager.data.source.local.LocalSource

fun LocalDestination.toExternal() = Destination(
    id = id,
    name = name,
    sourceId = sourceId,
    sourceName = sourceName,
//    date = date,
//    amount = amount.toString(),
)

fun Destination.toLocal() = LocalDestination(
    id = id,
    name = name,
    sourceId = sourceId,
    sourceName = sourceName,
//    date = date,
//    amount = amount.toString(),
)

fun LocalSource.toExternal() = Source(
    id = id,
    name = name,
)

fun Source.toLocal() = LocalSource(
    id = id,
    name = name,
)
