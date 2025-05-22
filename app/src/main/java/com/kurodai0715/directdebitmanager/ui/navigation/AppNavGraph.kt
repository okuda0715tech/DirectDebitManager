package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

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

        listDestination(
            onNavigateToEdit = { selectedItem ->
                navController.navigateToEditDestination(selectedItem)
            },
            onChangeTitle = onChangeTitle
        )

        editDestination(
            onNavigateUp = {
                navController.navigateUp()
            },
            onChangeTitle = onChangeTitle,
            onNavigateToDelComp = {
                navController.navigateToDelCompDestination()
            },
            onNavigateToSourceList = {
                navController.navigateToSourceListDestination()
            },
        )

        delCompDestination(
            onNavigateToList = {
                val hasList = navController.graph.findStartDestination().hasRoute<DestList>()
                val hasSourceList =
                    navController.graph.findStartDestination().hasRoute<SourceList>()

                if (hasList) {
                    navController.popUpToListDestination()
                } else if (hasSourceList) {
                    navController.popUpToSourceListDestination()
                }
            }
        )

        sourceListDestination(
            onNavigateToEdit = { selectedItem ->
                navController.navigateToSourceEditDestination(selectedItem)
            },
            onChangeTitle = onChangeTitle
        )

        sourceEditDestination(
            onNavigateUp = {
                navController.popUpToSourceListDestination()
            },
            onChangeTitle = onChangeTitle,
            onNavigateToDelComp = {
                navController.navigateToDelCompDestination()
            }
        )
    }
}