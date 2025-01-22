package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                navController.popUpToListDestination()
            },
            onChangeTitle = onChangeTitle,
            onNavigateToDelComp = {
                navController.navigateToDelCompDestination()
            }
        )

        delCompDestination(
            onNavigateToList = {
                navController.popUpToListDestination()
            }
        )

        sourceListDestination(
            onChangeTitle = onChangeTitle
        )

    }
}