/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Embedded

data class ChildWithParent(
    @Embedded(prefix = "child_")
    val child: TransferItemEntity,

    @Embedded(prefix = "parent_")
    val parent: TransferItemEntity,
)
