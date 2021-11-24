package com.example.nbrbcurrency.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = arrayOf("charCode", "position"), unique = true)])
data class CurrencySettingContainer constructor(
    var charCode: String = "",
    var scale: String = "",
    var isChecked: Boolean = false,
    var position: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
