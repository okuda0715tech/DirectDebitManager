package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalDestination
import com.kurodai0715.directdebitmanager.data.source.local.LocalTransSource

fun LocalDestination.toExternal() = Destination(
    id = id,
    destination = destination,
    source = source,
//    date = date,
//    amount = amount.toString(),
)

fun Destination.toLocal() = LocalDestination(
    id = id,
    destination = destination,
    source = source,
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
