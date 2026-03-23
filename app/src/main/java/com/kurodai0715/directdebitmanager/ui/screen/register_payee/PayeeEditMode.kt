package com.kurodai0715.directdebitmanager.ui.screen.register_payee

sealed interface PayeeEditMode {
    data object Add : PayeeEditMode
    data class Edit(val id: Int) : PayeeEditMode
}
