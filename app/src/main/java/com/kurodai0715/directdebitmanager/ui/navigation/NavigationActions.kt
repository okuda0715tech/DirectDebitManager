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

fun NavController.navigateToPayerEdit(sourceId: Int?) {
    navigate(
        PayerEdit(
            payerId = sourceId,
        )
    )
}

fun NavController.navigateToPayeeEditByAddMode() {
    navigate(
        PayeeEdit(id = null)
    )
}

fun NavController.navigateToPayeeEditByEditMode(id: Int) {
    navigate(
        PayeeEdit(id = id)
    )
}

fun NavController.navigateToSourceList() {
    navigate(SourceList)
}

fun NavController.navigateToPayeeList() {
    navigate(PayeeList)
}

fun NavController.navigateToPayerList() {
    navigate(PayerList)
}

fun NavController.navigateToTransferRelationList() {
    navigate(TransferRelationList)
}

fun NavController.navigateToTransferRelationEdit() {
    navigate(TransferRelationEdit)
}