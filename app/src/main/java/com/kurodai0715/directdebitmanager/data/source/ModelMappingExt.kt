package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalDestination
import com.kurodai0715.directdebitmanager.data.source.local.LocalTransSource

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

fun LocalTransSource.toExternal() = TransSource(
    id = id,
    source = source,
)

fun TransSource.toLocal() = LocalTransSource(
    id = id,
    source = source,
)
