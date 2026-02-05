/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import com.kurodai0715.directdebitmanager.data.source.local.TransferItemEntity
import com.kurodai0715.directdebitmanager.domain.model.ItemType
import com.kurodai0715.directdebitmanager.domain.model.SourceUiModel


fun TransferItemEntity.toSourceUiModel(): SourceUiModel {
    val type = requireNotNull(typeCode) { "LocalTransferItem.type is null" }

    return SourceUiModel(
        id = id,
        name = label,
        type = ItemType.fromInt(type),
    )
}

