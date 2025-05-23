package com.kurodai0715.directdebitmanager.ui.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.DestWithSource
import com.kurodai0715.directdebitmanager.data.source.Destination
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.ui.source_list.SourceListScreen
import com.kurodai0715.directdebitmanager.ui.delete_completion.DeleteCompletionDialog
import com.kurodai0715.directdebitmanager.ui.destination_list.DestinationListScreen
import com.kurodai0715.directdebitmanager.ui.destination_edit.DestinationEditScreen
import com.kurodai0715.directdebitmanager.ui.source_edit.SourceEditScreen
import kotlinx.serialization.Serializable

private const val TAG = "AppNavigation.kt"

@Serializable
data object DestList

@Serializable
data class DestEdit(
    val destId: Int? = null,
    val destName: String? = null,
    val sourceId: Int? = null,
    val sourceName: String? = null
)

@Serializable
data object DelComp

fun NavGraphBuilder.destListDestination(
    onNavigateToEdit: (DestWithSource?) -> Unit,
    onChangeTitle: (Int) -> Unit,
) {
    composable<DestList> {
        DestinationListScreen(onNavigateToEdit = onNavigateToEdit)
        onChangeTitle(R.string.list_screen_title)
    }
}

@Serializable
data object SourceList

@Serializable
data class SourceEdit(
    val sourceId: Int? = null,
    val sourceName: String? = null,
)

fun NavGraphBuilder.destEditDestination(
    onNavigateUp: () -> Unit,
    onChangeTitle: (Int) -> Unit,
    onNavigateToDelComp: () -> Unit,
    onNavigateToSourceList: () -> Unit,
) {
    composable<DestEdit> { backStackEntry ->
        val destEdit: DestEdit = backStackEntry.toRoute()
        DestinationEditScreen(
            destination = if (destEdit.destId == null) {
                null
            } else {
                Destination(id = destEdit.destId, name = destEdit.destName!!, sourceId = destEdit.sourceId!!,sourceName = destEdit.sourceName!!)
            },
            onNavigateUp = onNavigateUp,
            onNavigateToDelComp = onNavigateToDelComp,
            onNavigateToSourceList = onNavigateToSourceList,
        )

        Log.d(TAG, "edit.id = ${destEdit.destId}")
        val titleResId = if (destEdit.destId == null) {
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
    onNavigateToEdit: (Source?) -> Unit,
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
            source = if (sourceEdit.sourceId == null) {
                null
            } else {
                Source(id = sourceEdit.sourceId, name = sourceEdit.sourceName!!)
            },
            onNavigateUp = onNavigateUp,
            onNavigateToDelComp = onNavigateToDelComp,
        )

        Log.d(TAG, "sourceEdit.id = ${sourceEdit.sourceId}")

        val titleResId = if (sourceEdit.sourceId == null) {
            R.string.source_registration_title
        } else {
            R.string.source_update_title
        }

        onChangeTitle(titleResId)
    }
}

fun NavController.navigateToEditDestination(destWithSource: DestWithSource?) {
    navigate(
        DestEdit(
            destId = destWithSource?.destId,
            destName = destWithSource?.destName,
            sourceId = destWithSource?.sourceId,
            sourceName = destWithSource?.sourceName
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

fun NavController.navigateToSourceEditDestination(source: Source?) {
    navigate(
        SourceEdit(
            sourceId = source?.id,
            sourceName = source?.name
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

