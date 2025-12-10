/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.navigation.NavController


//private const val TAG = "NavigationActions.kt"

fun NavController.navigateToDestEdit(destId: Int?) {
    navigate(
        DestEdit(destId = destId)
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