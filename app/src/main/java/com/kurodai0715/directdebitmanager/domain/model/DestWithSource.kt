/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.domain.model

data class DestWithSource(
    val destId: Int,
    val destName: String,
    val destInputType: DestInputType,
    val sourceId: Int,
    val sourceName: String
)
