/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.dialog.source_selection

import com.kurodai0715.directdebitmanager.data.source.local.LocalTransferItem

fun List<LocalTransferItem>.toSourceSelectionUiModel(): List<SourceSelectionUiModel> {
    return map {
        SourceSelectionUiModel(
            sourceId = it.id,
            sourceName = it.label
        )
    }
}
