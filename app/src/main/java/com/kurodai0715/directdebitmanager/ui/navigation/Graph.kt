/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.screen.home.Screen as HomeScreen
import com.kurodai0715.directdebitmanager.ui.screen.payment_edit.Screen as PaymentEditScreen
import com.kurodai0715.directdebitmanager.ui.screen.payment_select.Screen as PaymentSelectScreen
import com.kurodai0715.directdebitmanager.ui.screen.payment_select.ViewModel as PaymentSelectViewModel
import com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit.Screen as TransferRelationEditScreen
import com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit.ViewModel as TransferRelationEditViewModel
import com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_list.Screen as TransferRelationListScreen

private const val TAG = "AppNavGraph.kt"

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier,
    onChangeTitle: (Int) -> Unit,
    startDestination: Any,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        composable<Home> {
            HomeScreen(
                onClickScreen = { navController.navigateToTransferRelationList() }
            )

            onChangeTitle(R.string.home_screen_title)
        }

        composable<TransferRelationList> {
            TransferRelationListScreen(
                openPaymentEdit = { navController.navigateToPaymentEdit() },
                onClickItem = { navController.navigateToTransferRelationEdit(it) }
            )

            onChangeTitle(R.string.transfer_relation_list_screen_title)
        }

        composable<PaymentEdit> { backStackEntry ->
            val paymentEdit: PaymentEdit = backStackEntry.toRoute()

            PaymentEditScreen(
                paymentId = paymentEdit.paymentId,
                onClickBack = { navController.navigateUp() }
            )

            onChangeTitle(R.string.payment_edit_screen_title)
        }

        composable<TransferRelationEdit> { backStackEntry ->
            val viewModel: TransferRelationEditViewModel =
                hiltViewModel(backStackEntry)

            TransferRelationEditScreen(
                viewModel = viewModel,
                onClickBack = {
                    navController.navigateUp()
                },
                openPaymentEdit = {
                    navController.navigateToPaymentEdit(it)
                },
                openPayerSelect = { paymentId ->
                    navController.navigateToPaymentSelect(
                        paymentId = paymentId,
                        target = Contract.SelectTarget.Payer,
                    )
                },
                openPayeeSelect = { paymentId ->
                    navController.navigateToPaymentSelect(
                        paymentId = paymentId,
                        target = Contract.SelectTarget.Payee,
                    )
                },
                onDeleted = {
                    navController.popToTransferRelationList()
                }
            )

            onChangeTitle(R.string.transfer_relation_edit_screen_title)
        }

        composable<PaymentSelect> { backStackEntry ->
            val paymentSelect: PaymentSelect = backStackEntry.toRoute()

            val paymentSelectViewModel: PaymentSelectViewModel =
                hiltViewModel(backStackEntry)

            PaymentSelectScreen(
                viewModel = paymentSelectViewModel,
                onClickBack = { navController.navigateUp() },
                backToRelationEditScreen = {
                    navController.popToTransferRelationEdit(paymentSelect.paymentId)
                }
            )

            val target = Contract.SelectTarget.valueOf(paymentSelect.target)

            when (target) {
                Contract.SelectTarget.Payer ->
                    onChangeTitle(R.string.payer_registration_screen_title)

                Contract.SelectTarget.Payee ->
                    onChangeTitle(R.string.payee_registration_screen_title)
            }
        }
    }
}