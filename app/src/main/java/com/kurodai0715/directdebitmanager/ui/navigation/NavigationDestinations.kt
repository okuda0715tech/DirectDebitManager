/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.navigation

import kotlinx.serialization.Serializable

//private const val TAG = "NavigationDestinations.kt"

/**
 * 全ての Destination のベースとなるオブジェクト.
 *
 * このオブジェクトが存在しないと、様々な Destination が渡される可能性がある部分 (例えばメニュー機能など) で、
 * Any 型を使うことになってしまう。
 * Any 型では制約がなさすぎて危険であるため、避ける必要がある。
 */
@Serializable
sealed interface NavDestination

@Serializable
data object Home : NavDestination

/**
 * 支払先関連の画面.
 *
 * この分類の仕方は暫定的であり、現時点では意味がない。
 */
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

@Serializable
sealed interface TransferBase : NavDestination

@Serializable
data object TransferRelationList : TransferBase


