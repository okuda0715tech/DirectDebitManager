/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.navigation

import kotlinx.serialization.Serializable

//private const val TAG = "NavigationDestinations.kt"

@Serializable
sealed interface NavDestination

@Serializable
data object DestList : NavDestination

@Serializable
data class DestEdit(
    val destId: Int? = null,
) : NavDestination

@Serializable
data object SourceList : NavDestination

@Serializable
data class SourceEdit(
    val sourceId: Int? = null,
) : NavDestination



