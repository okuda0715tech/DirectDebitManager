/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Embedded

data class TransferInfoLocal(
    @Embedded
    val destination: TransferItemEntity,

    @ColumnInfo(name = "source_name")
    val sourceName: String,
)
