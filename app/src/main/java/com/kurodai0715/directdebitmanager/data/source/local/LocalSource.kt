package com.kurodai0715.directdebitmanager.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "source")
data class LocalSource(
    /**
     * ID.
     *
     * ID に 0 が設定されたオブジェクトが Room の @Insert で挿入されると、
     * ID は自動採番されます。
     */
    @PrimaryKey(autoGenerate = true)
    val sourceId: Int = 0,

    val sourceName: String

)