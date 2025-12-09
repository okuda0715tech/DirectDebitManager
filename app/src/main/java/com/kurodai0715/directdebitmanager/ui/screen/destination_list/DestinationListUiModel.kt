/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.destination_list

data class NestedTreeItemUiModel(
    val destId: Int,
    val sourceId: Int,
    val label: String,
    val children: MutableList<NestedTreeItemUiModel> = mutableListOf(),
)

data class FlattenedTreeItemUiModel(
    val destId: Int,
    val label: String,
    val depth: Int,
)

enum class TabType() {
    ListView,
    TreeView;
}