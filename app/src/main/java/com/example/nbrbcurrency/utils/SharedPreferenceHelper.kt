package com.example.nbrbcurrency.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.nbrbcurrency.SettingsFragment

class SharedPreferenceHelper (private val context: Context) {

    companion object {
        const val FIRST_LAUNCH = "FIRST_LAUNCH"
    }

    private val settings : SharedPreferences by lazy {PreferenceManager.getDefaultSharedPreferences(context) }

    fun checkFirstLaunch():Boolean {
       return !settings.contains(FIRST_LAUNCH)
    }
}