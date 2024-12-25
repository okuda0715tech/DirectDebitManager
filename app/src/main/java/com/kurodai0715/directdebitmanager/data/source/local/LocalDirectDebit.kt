package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "direct_debit")
data class LocalDirectDebit(

    /**
     * ID.
     *
     * ID に 0 が設定されたオブジェクトが Room の @Insert で挿入されると、
     * ID は自動採番されます。
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val destination: String,

    val source: String,
//    val date: String,
//    val amount: Int,
)