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

fun NavController.navigateToPaymentEdit(paymentId: Int? = null) {
    navigate(PaymentEdit(paymentId = paymentId))
}

fun NavController.navigateToTransferRelationList() {
    navigate(TransferRelationList)
}

fun NavController.navigateToTransferRelationEdit(paymentId: Int) {
    navigate(TransferRelationEdit(paymentId))
}

fun NavController.navigateToPaymentSelect(
    paymentId: Int,
    target: NavContract.SelectTarget
) {
    navigate(PaymentSelect(paymentId = paymentId, target = target.route))
}

fun NavController.popToTransferRelationList() {
    popBackStack(route = TransferRelationList, inclusive = false)
}

fun NavController.popToTransferRelationEdit(paymentId: Int) {
    popBackStack(route = TransferRelationEdit(paymentId), inclusive = false)
}