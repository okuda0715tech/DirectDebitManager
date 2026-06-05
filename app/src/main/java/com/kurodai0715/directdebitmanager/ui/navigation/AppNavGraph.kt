/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.screen.destination_edit.DestinationEditScreen
import com.kurodai0715.directdebitmanager.ui.screen.destination_list.DestinationListScreen
import com.kurodai0715.directdebitmanager.ui.screen.home.HomeScreen
import com.kurodai0715.directdebitmanager.ui.screen.payee_edit.PayeeEditScreen
import com.kurodai0715.directdebitmanager.ui.screen.payee_list.PayeeListScreen
import com.kurodai0715.directdebitmanager.ui.screen.payer_edit.PayerEditScreen
import com.kurodai0715.directdebitmanager.ui.screen.payer_list.PayerListScreen
import com.kurodai0715.directdebitmanager.ui.screen.payment_select.ViewModel as PaymentSelectViewModel
import com.kurodai0715.directdebitmanager.ui.screen.source_edit.SourceEditScreen
import com.kurodai0715.directdebitmanager.ui.screen.source_list.SourceListScreen
import com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit.ViewModel as TransferRelationEditViewModel
import com.kurodai0715.directdebitmanager.ui.screen.payment_edit.Screen as PaymentEditScreen
import com.kurodai0715.directdebitmanager.ui.screen.payment_select.Screen as PaymentSelectScreen
import com.kurodai0715.directdebitmanager.ui.screen.transfer_relation_edit.Screen as TransferRelationEditScreen
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
                onClickPayerList = { navController.navigateToPayerList() },
                onClickPayeeList = { navController.navigateToPayeeList() },
                onClickRelationList = { navController.navigateToTransferRelationList() },
            )
            onChangeTitle(R.string.home_screen_title)
        }

        composable<PayeeList> {
            PayeeListScreen(
                onClickNavigateUp = { navController.navigateUp() },
                onClickAdd = {
                    navController.navigateToPayeeEditByAddMode()
                },
                onClickItem = { itemId ->
                    navController.navigateToPayeeEditByEditMode(itemId)
                }
            )
            onChangeTitle(R.string.payee_list_screen_title)
        }

        composable<PayeeEdit> { backStackEntry ->
            val payeeEdit: PayeeEdit = backStackEntry.toRoute()

            PayeeEditScreen(
                onClickBack = { navController.navigateUp() },
                payeeId = payeeEdit.id
            )
            onChangeTitle(R.string.payee_edit_screen_title)
        }

        composable<DestList> {
            DestinationListScreen(
                onClickDestEdit = { navController.navigateToDestEdit(it) }
            )
            onChangeTitle(R.string.list_screen_title)
        }

        composable<DestEdit> { backStackEntry ->
            val destEdit: DestEdit = backStackEntry.toRoute()
            DestinationEditScreen(
                destinationId = destEdit.destId,
                onClickNavigateUp = { navController.navigateUp() },
                onClickSourceList = { navController.navigateToSourceList() },
                onClickSourceEdit = { navController.navigateToSourceEdit(null) },
            )

            Log.d(TAG, "edit.id = ${destEdit.destId}")

            onChangeTitle(
                if (destEdit.destId == null)
                    R.string.register_screen_title
                else
                    R.string.edit_screen_title
            )
        }

        composable<SourceList> {
            SourceListScreen(
                onClickNavigateUp = { navController.navigateUp() },
                onClickSourceEdit = { navController.navigateToSourceEdit(it) }
            )
            onChangeTitle(R.string.source_list_title)
        }

        composable<PayerList> {
            PayerListScreen(
                onClickNavigateUp = { navController.navigateUp() },
                onClickSourceEdit = { navController.navigateToPayerEdit(it) }
            )
            onChangeTitle(R.string.payer_list_screen_title)
        }

        composable<SourceEdit> { backStackEntry ->
            val sourceEdit: SourceEdit = backStackEntry.toRoute()

            SourceEditScreen(
                sourceId = sourceEdit.sourceId,
                onClickNavigateUp = { navController.navigateUp() },
            )

            Log.d(TAG, "sourceEdit.id = ${sourceEdit.sourceId}")

            onChangeTitle(
                if (sourceEdit.sourceId == null)
                    R.string.source_registration_title
                else
                    R.string.source_update_title
            )
        }

        composable<PayerEdit> { backStackEntry ->
            val payerEdit: PayerEdit = backStackEntry.toRoute()

            PayerEditScreen(
                sourceId = payerEdit.payerId,
                onClickNavigateUp = { navController.navigateUp() },
            )

            Log.d(TAG, "sourceEdit.id = ${payerEdit.payerId}")

            onChangeTitle(
                if (payerEdit.payerId == null)
                    R.string.payer_registration_title
                else
                    R.string.payer_update_title
            )
        }

        composable<TransferRelationList> {
            TransferRelationListScreen(
                onClickBack = { navController.navigateUp() },
                onClickAdd = { navController.navigateToPaymentEdit() },
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
                    // PaymentEdit と TransferRelationList の間に PaymentEditGraph が
                    // 存在しているはずなので、それも含めて破棄するため、
                    // navController.navigateUp() ではなく、 popBackStack() を使う。
                    navController.popToTransferRelationList()
                },
                openPaymentEdit = {
                    navController.navigateToPaymentEdit(it)
                },
                openPayerSelect = { paymentId ->
                    navController.navigateToPaymentSelect(
                        paymentId = paymentId,
                        target = NavContract.SelectTarget.Payer,
                    )
                },
                openPayeeSelect = { paymentId ->
                    navController.navigateToPaymentSelect(
                        paymentId = paymentId,
                        target = NavContract.SelectTarget.Payee,
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
                onClickBack = { navController.navigateUp() }
            )

            val target = NavContract.SelectTarget.valueOf(paymentSelect.target)

            when (target) {
                NavContract.SelectTarget.Payer ->
                    onChangeTitle(R.string.payer_registration_screen_title)

                NavContract.SelectTarget.Payee ->
                    onChangeTitle(R.string.payee_registration_screen_title)
            }
        }
    }
}