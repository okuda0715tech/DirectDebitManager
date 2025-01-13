package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.DirectDebit
import com.kurodai0715.directdebitmanager.ui.direct_debit_list.DirectDebitListScreen
import com.kurodai0715.directdebitmanager.ui.edit_direct_debit.EditDirectDebitScreen
import kotlinx.serialization.Serializable

@Serializable
object List

@Serializable
data class Edit(
    val id: Int? = null,
    val dest: String? = null,
    val source: String? = null
)

fun NavGraphBuilder.listDestination(
    onNavigateToEdit: (DirectDebit?) -> Unit,
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
    composable<Edit> { backStackEntry ->
        val edit: Edit = backStackEntry.toRoute()
        EditDirectDebitScreen(
            directDebit = if (edit.id == null) {
                null
            } else {
                DirectDebit(id = edit.id, destination = edit.dest!!, source = edit.source!!)
            },
            onNavigateUp = onNavigateUp
        )
        onChangeTitle(R.string.edit_screen_title)
    }
}

fun NavController.navigateToEditDestination(directDebit: DirectDebit?) {
    navigate(
        Edit(
            id = directDebit?.id,
            dest = directDebit?.destination,
            source = directDebit?.source
        )
    )
}
