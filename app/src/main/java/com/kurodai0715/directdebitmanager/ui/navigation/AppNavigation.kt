package com.kurodai0715.directdebitmanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHost
import androidx.navigation.compose.composable
import com.kurodai0715.directdebitmanager.ui.direct_debit_list.DirectDebitListScreen
import com.kurodai0715.directdebitmanager.ui.edit_direct_debit.EditDirectDebitScreen
import kotlinx.serialization.Serializable

@Serializable
object List

@Serializable
object Edit

fun NavGraphBuilder.listDestination() {
    composable<List> {
        DirectDebitListScreen()
    }
}

fun NavGraphBuilder.editDestination() {
    composable<Edit> {
        EditDirectDebitScreen()
    }
}


