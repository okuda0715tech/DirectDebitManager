/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.destination_edit

import com.kurodai0715.directdebitmanager.domain.model.Source


fun Source.toSourceUiModel() = SourceUiModel(
    id = id,
    name = name,
    type = type,
    typeEnum = typeEnum
)


