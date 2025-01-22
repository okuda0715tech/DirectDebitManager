package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transfer_source")
data class LocalTransSource(
    /**
     * ID.
     *
     * ID に 0 が設定されたオブジェクトが Room の @Insert で挿入されると、
     * ID は自動採番されます。
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val source: String

)