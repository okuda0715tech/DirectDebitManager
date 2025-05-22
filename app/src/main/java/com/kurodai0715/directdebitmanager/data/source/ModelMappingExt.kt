package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalDestination
import com.kurodai0715.directdebitmanager.data.source.local.LocalSource

fun LocalDestination.toExternal() = Destination(
    destId = destId,
    destName = destName,
    sourceName = sourceName,
//    date = date,
//    amount = amount.toString(),
)

fun Destination.toLocal() = LocalDestination(
    destId = destId,
    destName = destName,
    sourceName = sourceName,
//    date = date,
//    amount = amount.toString(),
)

fun LocalSource.toExternal() = Source(
    sourceId = this@toExternal.sourceId,
    source = sourceName,
)

fun Source.toLocal() = LocalSource(
    sourceId = sourceId,
    sourceName = source,
)
