/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.source_edit

import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.ItemType


fun getSourceTypeStringRes(type: ItemType): Int =
    when (type) {
        ItemType.Bank -> R.string.bank
        ItemType.CreditCard -> R.string.credit_card
        ItemType.DebitCard -> R.string.debit_card
        ItemType.Others -> R.string.others
    }


