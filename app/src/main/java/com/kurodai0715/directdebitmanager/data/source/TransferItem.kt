package com.kurodai0715.directdebitmanager.data.source

import com.kurodai0715.directdebitmanager.data.source.local.LocalTransferItem
import com.kurodai0715.directdebitmanager.domain.SourceType

data class TransferItem(

    /**
     * ID.
     *
     * ID に 0 が設定されたオブジェクトが Room の @Insert で挿入されると、
     * ID は自動採番されます。
     */
    val id: Int = 0,

    /**
     * 振替元、もしくは、振替先の名称.
     */
    val label: String,

    /**
     * 振替元の区分.
     *
     * [SourceType] を参照。
     */
    val sourceType: Int?,

    /**
     * この [LocalTransferItem] の振替元の [LocalTransferItem] の ID .
     *
     * null の場合、この [LocalTransferItem] は、振替元を持たない（つまり、ルートである）ことを意味する。
     */
    val parentId: Int?,

//    val date: String,
//    val amount: Int,

)
