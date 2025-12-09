/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

//private const val TAG = "AppNavGraph.kt"

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

        destListDestination(
            onNavigateToEdit = { selectedItem ->
                navController.navigateToEditDestination(selectedItem)
            },
            onChangeTitle = onChangeTitle
        )

        destEditDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onChangeTitle = onChangeTitle,
            onNavigateToSourceList = {
                navController.navigateToSourceListDestination()
            },
            onNavigateToSourceEdit = {
                navController.navigateToSourceEditDestination(null)
            }
        )

        sourceListDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onNavigateToEdit = { selectedItem ->
                navController.navigateToSourceEditDestination(selectedItem)
            },
            onChangeTitle = onChangeTitle
        )

        sourceEditDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onChangeTitle = onChangeTitle
        )
    }
}