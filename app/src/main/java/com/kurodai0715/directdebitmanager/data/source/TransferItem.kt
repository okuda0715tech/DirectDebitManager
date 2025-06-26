package com.kurodai0715.directdebitmanager.data.source

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
     * この  [TransferItem] が振替元として登録されたかどうかを判別するためのもの.
     *
     * 振替元として登録された場合は true 。そうでなければ false 。
     */
    val isSourceItem: Boolean,

    /**
     * この [TransferItem] の区分.
     *
     * 具体的な区分の種類は、 [SourceType] を参照。
     */
    val type: Int?,

    /**
     * この [TransferItem] の振替元の [TransferItem] の ID .
     *
     * null の場合、この [TransferItem] は、振替元を持たない（つまり、ルートである）ことを意味する。
     */
    val sourceId: Int?,

//    val date: String,
//    val amount: Int,

)
