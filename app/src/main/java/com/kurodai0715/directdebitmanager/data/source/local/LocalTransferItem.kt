/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kurodai0715.directdebitmanager.domain.model.TransferItemType

@Entity(tableName = "transfer_item")
data class LocalTransferItem(

    /**
     * ID.
     *
     * ID に 0 が設定されたオブジェクトが Room の @Insert で挿入されると、
     * ID は自動採番されます。
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    /**
     * 振替元、もしくは、振替先の名称.
     */
    val label: String,

    /**
     * この  [LocalTransferItem] が振替元として登録されたかどうかを判別するためのもの.
     *
     * 振替元として登録された場合は true 。そうでなければ false 。
     */
    val isSourceItem: Boolean,

    /**
     * この [LocalTransferItem] の区分.
     *
     * 具体的な区分の種類は、 [TransferItemType] を参照。
     */
    val type: Int?,

    /**
     * この [LocalTransferItem] の振替元の [LocalTransferItem] の ID .
     *
     * 0 の場合、この [LocalTransferItem] は、振替元を持たない（つまり、ルートである）ことを意味する。
     */
    val parentId: Int = 0,

//    val date: String,
//    val amount: Int,

)
