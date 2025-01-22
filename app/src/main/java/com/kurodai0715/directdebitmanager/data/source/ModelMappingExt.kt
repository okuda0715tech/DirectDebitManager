package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalDirectDebit
import com.kurodai0715.directdebitmanager.data.source.local.LocalTransSource

fun LocalDirectDebit.toExternal() = DirectDebit(
    id = id,
    destination = destination,
    source = source,
//    date = date,
//    amount = amount.toString(),
)

fun DirectDebit.toLocal() = LocalDirectDebit(
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
