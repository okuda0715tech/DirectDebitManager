/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.R.string.edit_screen_title
import com.kurodai0715.directdebitmanager.R.string.register_screen_title
import com.kurodai0715.directdebitmanager.R.string.source_list_title
import com.kurodai0715.directdebitmanager.R.string.source_registration_title
import com.kurodai0715.directdebitmanager.R.string.source_update_title
import com.kurodai0715.directdebitmanager.ui.screen.destination_edit.DestinationEditScreen
import com.kurodai0715.directdebitmanager.ui.screen.destination_list.DestinationListScreen
import com.kurodai0715.directdebitmanager.ui.screen.source_edit.SourceEditScreen
import com.kurodai0715.directdebitmanager.ui.screen.source_list.SourceListScreen

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
                    register_screen_title
                else
                    edit_screen_title
            )
        }

        composable<SourceList> {
            SourceListScreen(
                onClickNavigateUp = { navController.navigateUp() },
                onClickSourceEdit = { navController.navigateToSourceEdit(it) }
            )
            onChangeTitle(source_list_title)
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
                    source_registration_title
                else
                    source_update_title
            )
        }
    }
}