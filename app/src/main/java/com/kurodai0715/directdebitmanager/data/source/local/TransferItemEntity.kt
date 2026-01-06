/*
 * Copyright (c) 2025 Okuda Tomohiro
 * Licensed under the MIT License.
 */

package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kurodai0715.directdebitmanager.domain.model.ItemType

@Entity(tableName = "transfer_item")
data class TransferItemEntity(

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
     * この  [TransferItemEntity] が振替元として登録されたかものどうかを判別するためのもの.
     *
     * 振替元として登録された場合は true 。そうでなければ false 。
     */
    val isSourceItem: Boolean,

    // TODO type ではなく、 typeCode という名前に変更する。
    /**
     * この [TransferItemEntity] の区分.
     *
     * 具体的な区分の種類は、 [ItemType] を参照。
     */
    val type: Int?,

    /**
     * この [TransferItemEntity] の振替元の [TransferItemEntity] の ID .
     *
     * 0 の場合、この [TransferItemEntity] は、振替元を持たない（つまり、ルートである）ことを意味する。
     */
    val parentId: Int = 0,

//    val date: String,
//    val amount: Int,

)
