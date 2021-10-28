package com.example.nbrbcurrency.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = arrayOf(Index(value = arrayOf("charCode, position"), unique = true)) )
data class CurrencySettingContainer constructor(
    @PrimaryKey(autoGenerate = false)
    var id: Int,
    var charCode: String,
    var scale: String,
    var isChecked: Boolean,
    var position: Int
)
