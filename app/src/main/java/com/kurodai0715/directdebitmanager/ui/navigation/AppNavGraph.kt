package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = List,
        modifier = modifier,
    ) {

        listDestination(onNavigateToEdit = {
            navController.navigateToEditDestination()
        })

        editDestination()

    }
}