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
data object TransferRelationList : NavDestination

@Serializable
data class TransferRelationEdit(
    val paymentId: Int,
) : NavDestination

@Serializable
data class PaymentSelect(
    val paymentId: Int,
    val target: String,
) : NavDestination

@Serializable
data class PaymentEdit(
    val paymentId: Int?
) : NavDestination