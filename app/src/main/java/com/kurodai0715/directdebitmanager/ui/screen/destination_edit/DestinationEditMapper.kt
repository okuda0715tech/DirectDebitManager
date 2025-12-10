/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import com.kurodai0715.directdebitmanager.data.source.local.LocalTransferItem
import com.kurodai0715.directdebitmanager.domain.TransferItemType
import com.kurodai0715.directdebitmanager.domain.model.Source


fun Source.toSourceUiModel() = SourceUiModel(
    id = id,
    name = name,
    type = type,
    typeEnum = typeEnum
)

fun LocalTransferItem.toSourceUiModel(): SourceUiModel {
    val type = requireNotNull(type) { "LocalTransferItem.type is null" }

    return SourceUiModel(
        id = id,
        name = label,
        typeEnum = TransferItemType.Companion.fromInt(type),
        type = type,
    )
}