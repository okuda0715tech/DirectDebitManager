/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.navigation.NavController
import com.kurodai0715.directdebitmanager.ui.screen.destination_list.DestWithSourceUiModel


//private const val TAG = "NavigationActions.kt"

// TODO DestWithSourceUiModel への依存を解消する
fun NavController.navigateToDestEdit(destWithSourceUiModel: DestWithSourceUiModel?) {
    navigate(
        DestEdit(destId = destWithSourceUiModel?.destId)
    )
}

fun NavController.navigateToSourceEdit(sourceId: Int?) {
    navigate(
        SourceEdit(
            sourceId = sourceId,
        )
    )
}

fun NavController.navigateToSourceList() {
    navigate(SourceList)
}