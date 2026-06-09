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
sealed interface TransferBase : NavDestination

@Serializable
data object TransferRelationList : TransferBase

@Serializable
data class TransferRelationEdit(
    val paymentId: Int,
) : TransferBase

@Serializable
data class PaymentSelect(
    val paymentId: Int,
    val target: String,
) : TransferBase

@Serializable
data class PaymentEdit(
    val paymentId: Int?
) : NavDestination