package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class DestWithSourceLocal(
    @Embedded
    val destination: LocalTransferItem,

    @ColumnInfo(name = "source_name")
    val sourceName: String,
)
