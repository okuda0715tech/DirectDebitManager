/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.navigation

import kotlinx.serialization.Serializable

//private const val TAG = "NavigationDestinations.kt"

@Serializable
data object DestList

@Serializable
data class DestEdit(
    val destId: Int? = null,
)

@Serializable
data object SourceList

@Serializable
data class SourceEdit(
    val sourceId: Int? = null,
)



