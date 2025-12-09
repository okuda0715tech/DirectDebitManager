/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalTransferItem
import com.kurodai0715.directdebitmanager.domain.TransferItemType
import com.kurodai0715.directdebitmanager.domain.model.Source
import com.kurodai0715.directdebitmanager.ui.screen.destination_edit.SourceUiModel

fun LocalTransferItem.toSource(): Source {
    val type = requireNotNull(type) { "LocalTransferItem.type is null" }

    return Source(
        id = id,
        name = label,
        typeEnum = TransferItemType.fromInt(type),
        type = type,
    )
}

fun LocalTransferItem.toSourceUiModel(): SourceUiModel {
    val type = requireNotNull(type) { "LocalTransferItem.type is null" }

    return SourceUiModel(
        id = id,
        name = label,
        typeEnum = TransferItemType.fromInt(type),
        type = type,
    )
}

fun LocalTransferItem.toExternal() = TransferItem(
    id = id,
    label = label,
    isSourceItem = isSourceItem,
    type = type,
    sourceId = parentId,
)
