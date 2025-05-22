package com.kurodai0715.directdebitmanager.ui.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.Destination
import com.kurodai0715.directdebitmanager.data.source.TransSource
import com.kurodai0715.directdebitmanager.ui.source_list.SourceListScreen
import com.kurodai0715.directdebitmanager.ui.delete_completion.DeleteCompletionDialog
import com.kurodai0715.directdebitmanager.ui.direct_debit_list.DirectDebitListScreen
import com.kurodai0715.directdebitmanager.ui.edit_direct_debit.EditDirectDebitScreen
import com.kurodai0715.directdebitmanager.ui.source_edit.SourceEditScreen
import kotlinx.serialization.Serializable

private const val TAG = "AppNavigation.kt"

@Serializable
data object DestList

@Serializable
data class DestEdit(
    val id: Int? = null,
    val dest: String? = null,
    val source: String? = null
)

@Serializable
data object DelComp

fun NavGraphBuilder.listDestination(
    onNavigateToEdit: (Destination?) -> Unit,
    onChangeTitle: (Int) -> Unit,
) {
    composable<DestList> {
        DirectDebitListScreen(onNavigateToEdit = onNavigateToEdit)
        onChangeTitle(R.string.list_screen_title)
    }
}

@Serializable
data object SourceList

@Serializable
data class SourceEdit(
    val id: Int? = null,
    val source: String? = null,
)

fun NavGraphBuilder.editDestination(
    onNavigateUp: () -> Unit,
    onChangeTitle: (Int) -> Unit,
    onNavigateToDelComp: () -> Unit,
    onNavigateToSourceList: () -> Unit,
) {
    composable<DestEdit> { backStackEntry ->
        val destEdit: DestEdit = backStackEntry.toRoute()
        EditDirectDebitScreen(
            destination = if (destEdit.id == null) {
                null
            } else {
                Destination(id = destEdit.id, destination = destEdit.dest!!, source = destEdit.source!!)
            },
            onNavigateUp = onNavigateUp,
            onNavigateToDelComp = onNavigateToDelComp,
            onNavigateToSourceList = onNavigateToSourceList,
        )

        Log.d(TAG, "edit.id = ${destEdit.id}")
        val titleResId = if (destEdit.id == null) {
            R.string.register_screen_title
        } else {
            R.string.edit_screen_title
        }
        onChangeTitle(titleResId)
    }
}

fun NavGraphBuilder.delCompDestination(onNavigateToList: () -> Unit) {
    dialog<DelComp> {
        DeleteCompletionDialog(onNavigateToList = onNavigateToList)
    }
}

fun NavGraphBuilder.sourceListDestination(
    onNavigateToEdit: (TransSource?) -> Unit,
    onChangeTitle: (Int) -> Unit,
) {
    composable<SourceList> {
        SourceListScreen(onNavigateToEdit = onNavigateToEdit)
        onChangeTitle(R.string.source_list_title)
    }
}

fun NavGraphBuilder.sourceEditDestination(
    onNavigateUp: () -> Unit,
    onChangeTitle: (Int) -> Unit,
    onNavigateToDelComp: () -> Unit,
) {
    composable<SourceEdit> { backStackEntry ->
        val sourceEdit: SourceEdit = backStackEntry.toRoute()

        SourceEditScreen(
            transSource = if (sourceEdit.id == null) {
                null
            } else {
                TransSource(id = sourceEdit.id, source = sourceEdit.source!!)
            },
            onNavigateUp = onNavigateUp,
            onNavigateToDelComp = onNavigateToDelComp,
        )

        Log.d(TAG, "sourceEdit.id = ${sourceEdit.id}")

        val titleResId = if (sourceEdit.id == null) {
            R.string.source_registration_title
        } else {
            R.string.source_update_title
        }

        onChangeTitle(titleResId)
    }
}

fun NavController.navigateToEditDestination(destination: Destination?) {
    navigate(
        DestEdit(
            id = destination?.id,
            dest = destination?.destination,
            source = destination?.source
        )
    )
}

fun NavController.navigateToDelCompDestination() {
    navigate(DelComp)
}

fun NavController.popUpToListDestination() {
    navigate(DestList) {
        popUpTo(DestList)
        launchSingleTop = true
    }
}

fun NavController.navigateToSourceEditDestination(transSource: TransSource?) {
    navigate(
        SourceEdit(
            id = transSource?.id,
            source = transSource?.source
        )
    )
}

fun NavController.popUpToSourceListDestination() {
    navigate(SourceList) {
        popUpTo(SourceList)
        launchSingleTop = true
    }
}

fun NavController.navigateToSourceListDestination() {
    navigate(SourceList)
}

