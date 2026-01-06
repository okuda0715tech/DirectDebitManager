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

fun TransferInfo.toDestInputSourceList(): DestInput.Source {
    return DestInput.Source(destId = destId, name = destName, type = destAccountType)
}

fun TransferInfo.toDestInputKeyboard(): DestInput.Keyboard {
    return DestInput.Keyboard(destId = destId, name = destName)
}

