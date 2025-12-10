/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.navigation.NavController
import com.kurodai0715.directdebitmanager.ui.screen.destination_list.DestWithSourceUiModel
import kotlinx.serialization.Serializable

private const val TAG = "AppNavigation.kt"

@Serializable
data object DestList

@Serializable
data class DestEdit(
    val destId: Int? = null,
)

@Serializable
data object SourceList

@Serializable
data class SourceEdit(
    val sourceId: Int? = null,
)

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

