package com.example.nbrbcurrency.db

import androidx.room.*
import io.reactivex.rxjava3.core.Single

@Dao
interface SettingsDao {

    @Query("SELECT * from CurrencySettingContainer ORDER BY position")
    fun getAll(): Single<List<CurrencySettingContainer>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(settings : CurrencySettingContainer)

    @Update
    fun update(settings : CurrencySettingContainer)
}