/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.navigation.NavController


//private const val TAG = "NavigationActions.kt"

fun NavController.navigateToTransferRelationList() {
    navigate(TransferRelationList)
}

fun NavController.navigateToPaymentEdit(paymentId: Int? = null) {
    navigate(PaymentEdit(paymentId = paymentId))
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