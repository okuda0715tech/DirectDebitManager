package com.kurodai0715.directdebitmanager.ui.screen.payment_edit

fun UiState.idToInt(): Int? {
    return when (this.id) {
        is UiState.Id.Assigned -> this.id.value
        is UiState.Id.Unassigned -> null
    }
}