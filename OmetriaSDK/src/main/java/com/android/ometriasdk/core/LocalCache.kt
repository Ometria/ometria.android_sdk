package com.android.ometriasdk.core

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by cristiandregan
 * on 24/07/2020.
 */

private const val LOCAL_CACHE_PREFERENCES = "LOCAL_CACHE_PREFERENCES"
private const val IS_FIRST_APP_RUN = "IS_FIRST_APP_RUN"

internal class LocalCache(private val context: Context) {

    fun isFirstAppRun(isFirstAppRun: Boolean) {
        getSettingsPreferences().edit().putBoolean(IS_FIRST_APP_RUN, isFirstAppRun).apply()
    }

    fun isFirstAppRun(): Boolean {
        return getSettingsPreferences().getBoolean(IS_FIRST_APP_RUN, true)
    }

    private fun getSettingsPreferences(): SharedPreferences {
        return context.getSharedPreferences(LOCAL_CACHE_PREFERENCES, Context.MODE_PRIVATE)
    }
}