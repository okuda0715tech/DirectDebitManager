package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier,
    onChangeTitle: (Int) -> Unit,
) {
    NavHost(
        navController = navController,
        startDestination = List,
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
                navController.popBackStack()
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

    }
}