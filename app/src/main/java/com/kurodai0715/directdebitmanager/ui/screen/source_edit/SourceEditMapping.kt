/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.ui.screen.source_edit

import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.model.TransferItemType


fun getSourceTypeStringRes(type: TransferItemType): Int =
    when (type) {
        TransferItemType.Bank -> R.string.bank
        TransferItemType.CreditCard -> R.string.credit_card
        TransferItemType.DebitCard -> R.string.debit_card
        TransferItemType.Others -> R.string.others
    }

fun getSourceTypeStringRes(type: Int): Int = getSourceTypeStringRes(TransferItemType.fromInt(type))


