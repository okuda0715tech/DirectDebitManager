/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.domain.TransferItemType

data class Source(
    /**
     * ID.
     *
     * ID を自動採番したい場合は 0 を設定してください。
     */
    val id: Int = 0,

    val name: String,

    val type: Int,

    val typeEnum: TransferItemType,
)

