package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalDirectDebit

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

