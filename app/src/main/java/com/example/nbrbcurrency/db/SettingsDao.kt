package com.example.nbrbcurrency.db

import androidx.room.Dao
import androidx.room.Query
import io.reactivex.rxjava3.core.Single

@Dao
interface SettingsDao {

    @Query("SELECT * from CurrencySettingContainer")
    fun getAll(): Single<List<CurrencySettingContainer>>
}