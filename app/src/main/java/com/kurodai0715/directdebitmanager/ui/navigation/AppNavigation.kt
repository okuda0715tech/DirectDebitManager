package com.kurodai0715.directdebitmanager.ui.navigation

import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.data.source.Source
import com.kurodai0715.directdebitmanager.ui.screen.destination_edit.DestinationEditScreen
import com.kurodai0715.directdebitmanager.ui.screen.destination_list.DestWithSourceUiModel
import com.kurodai0715.directdebitmanager.ui.screen.destination_list.DestinationListScreen
import com.kurodai0715.directdebitmanager.ui.screen.source_edit.SourceEditScreen
import com.kurodai0715.directdebitmanager.ui.screen.source_list.SourceListScreen
import kotlinx.serialization.Serializable

private const val TAG = "AppNavigation.kt"

@Serializable
data object DestList

@Serializable
data class DestEdit(
    val destId: Int? = null,
)

fun NavGraphBuilder.destListDestination(
    onNavigateToEdit: (DestWithSourceUiModel?) -> Unit,
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
    val sourceType: Int? = null,
)

fun NavGraphBuilder.destEditDestination(
    onNavigateUp: () -> Unit,
    onChangeTitle: (Int) -> Unit,
    onNavigateToSourceList: () -> Unit,
    onNavigateToSourceEdit: () -> Unit,
) {
    composable<DestEdit> { backStackEntry ->
        val destEdit: DestEdit = backStackEntry.toRoute()
        DestinationEditScreen(
            destinationId = destEdit.destId,
            onNavigateUp = onNavigateUp,
            onNavigateToSourceList = onNavigateToSourceList,
            onNavigateToSourceEdit = onNavigateToSourceEdit,
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

fun NavGraphBuilder.sourceListDestination(
    onNavigateUp: () -> Unit,
    onNavigateToEdit: (Source?) -> Unit,
    onChangeTitle: (Int) -> Unit,
) {
    composable<SourceList> {
        SourceListScreen(
            onNavigateUp = onNavigateUp,
            onNavigateToEdit = onNavigateToEdit
        )
        onChangeTitle(R.string.source_list_title)
    }
}

fun NavGraphBuilder.sourceEditDestination(
    onNavigateUp: () -> Unit,
    onChangeTitle: (Int) -> Unit,
) {
    composable<SourceEdit> { backStackEntry ->
        val sourceEdit: SourceEdit = backStackEntry.toRoute()

        SourceEditScreen(
            sourceId = sourceEdit.sourceId,
            sourceName = sourceEdit.sourceName,
            sourceType = sourceEdit.sourceType,
            onNavigateUp = onNavigateUp,
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

fun NavController.navigateToEditDestination(destWithSourceUiModel: DestWithSourceUiModel?) {
    navigate(
        DestEdit(destId = destWithSourceUiModel?.destId)
    )
}

fun NavController.navigateToSourceEditDestination(source: Source?) {
    navigate(
        SourceEdit(
            sourceId = source?.id,
            sourceName = source?.name,
            sourceType = source?.type,
        )
    )
}

fun NavController.navigateToSourceListDestination() {
    navigate(SourceList)
}

