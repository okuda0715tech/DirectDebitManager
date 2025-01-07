package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.ui.direct_debit_list.DirectDebitListScreen
import com.kurodai0715.directdebitmanager.ui.edit_direct_debit.EditDirectDebitScreen
import kotlinx.serialization.Serializable

@Serializable
object List

@Serializable
object Edit

fun NavGraphBuilder.listDestination(
    onNavigateToEdit: () -> Unit,
    onChangeTitle: (Int) -> Unit,
) {
    composable<List> {
        DirectDebitListScreen(onNavigateToEdit = onNavigateToEdit)
        onChangeTitle(R.string.list_screen_title)
    }
}

fun NavGraphBuilder.editDestination(
    onNavigateUp: () -> Unit,
    onChangeTitle: (Int) -> Unit,
) {
    composable<Edit> {
        EditDirectDebitScreen(onNavigateUp = onNavigateUp)
        onChangeTitle(R.string.edit_screen_title)
    }
}

fun NavController.navigateToEditDestination() {
    navigate(Edit)
}
