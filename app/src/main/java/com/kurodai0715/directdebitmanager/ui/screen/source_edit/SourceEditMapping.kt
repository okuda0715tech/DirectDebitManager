package com.kurodai0715.directdebitmanager.ui.screen.source_edit

import com.kurodai0715.directdebitmanager.R
import com.kurodai0715.directdebitmanager.domain.SourceType


fun getSourceTypeStringRes(type: SourceType): Int =
    when (type) {
        SourceType.Bank -> R.string.bank
        SourceType.CreditCard -> R.string.credit_card
        SourceType.DebitCard -> R.string.debit_card
        SourceType.Others -> R.string.others
    }

fun getSourceTypeStringRes(type: Int): Int = getSourceTypeStringRes(SourceType.fromInt(type))


