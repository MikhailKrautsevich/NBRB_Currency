package com.example.nbrbcurrency.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CurrencySettingContainer::class], version = 1)
abstract class AppDataBase: RoomDatabase() {
    abstract val dao : SettingsDao
}