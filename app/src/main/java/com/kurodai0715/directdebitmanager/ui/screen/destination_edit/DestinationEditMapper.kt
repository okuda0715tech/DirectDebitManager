/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import com.kurodai0715.directdebitmanager.data.source.local.TransferItemEntity
import com.kurodai0715.directdebitmanager.domain.model.SourceUiModel
import com.kurodai0715.directdebitmanager.domain.model.TransferInfo
import com.kurodai0715.directdebitmanager.domain.model.ItemType


fun TransferItemEntity.toSourceUiModel(): SourceUiModel {
    val type = requireNotNull(type) { "LocalTransferItem.type is null" }

    return SourceUiModel(
        id = id,
        name = label,
        type = ItemType.fromInt(type),
    )
}

fun TransferInfo.toDestInputSourceList(): DestInput.Existing {
    return DestInput.Existing(destId = destId, name = destName, type = destAccountType)
}

fun TransferInfo.toDestInputKeyboard(): DestInput.New {
    return DestInput.New(destId = destId, name = destName)
}

