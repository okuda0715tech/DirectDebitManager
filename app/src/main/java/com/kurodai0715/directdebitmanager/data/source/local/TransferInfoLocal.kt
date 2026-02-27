/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.kurodai0715.directdebitmanager.domain.model.DestInputType
import com.kurodai0715.directdebitmanager.domain.model.TransferInfo

data class TransferInfoLocal(
    @Embedded
    val destination: TransferItemEntity,

    @ColumnInfo(name = "source_name")
    val sourceName: String,
)

fun TransferInfoLocal.toTransferInfo(): TransferInfo {
    val inputType =
        if (destination.isSourceItem) DestInputType.SourceList else DestInputType.Keyboard

    return TransferInfo(
        destId = destination.id,
        destName = destination.label,
        inputType = inputType,
        sourceId = destination.parentId,
        sourceName = sourceName,
    )
}