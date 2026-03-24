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
data object Home : NavDestination

@Serializable
sealed interface PayeeBase : NavDestination

@Serializable
data object PayeeList : PayeeBase

@Serializable
data class PayeeEdit(
    val id: Int?
) : PayeeBase

@Serializable
sealed interface DestBase : NavDestination

@Serializable
data object DestList : DestBase

@Serializable
data class DestEdit(
    val destId: Int? = null,
) : DestBase

@Serializable
sealed interface SourceBase : NavDestination

@Serializable
data object SourceList : SourceBase

@Serializable
data class SourceEdit(
    val sourceId: Int? = null,
) : SourceBase

@Serializable
sealed interface PayerBase : NavDestination

@Serializable
data object PayerList : PayerBase

@Serializable
data class PayerEdit(
    val payerId: Int? = null,
) : PayerBase



