package com.example.nbrbcurrency.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CurrencySettingContainer::class], version = 1)
abstract class SettingsDataBase : RoomDatabase() {
    abstract val dao: SettingsDao

    companion object {
        const val SETTINGS_DB = "SETTINGS_DB"

        @Volatile
        private var INSTANCE: SettingsDataBase? = null

        fun getDatabase(context: Context): SettingsDataBase {
            val tempInstance = INSTANCE

            tempInstance?.let { return tempInstance }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SettingsDataBase::class.java,
                    SETTINGS_DB
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}