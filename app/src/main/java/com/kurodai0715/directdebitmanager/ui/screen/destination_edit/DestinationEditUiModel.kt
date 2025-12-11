package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import com.kurodai0715.directdebitmanager.domain.model.TransferItemType

data class SourceUiModel(
    /**
     * ID.
     *
     * ID を自動採番したい場合は 0 を設定してください。
     */
    val id: Int = 0,

    val name: String,

    val typeEnum: TransferItemType,
)