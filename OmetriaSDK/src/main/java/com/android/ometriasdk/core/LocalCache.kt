package com.android.ometriasdk.core

import android.content.Context
import android.content.SharedPreferences

/**
 * Created by cristiandregan
 * on 24/07/2020.
 */

private const val LOCAL_CACHE_PREFERENCES = "LOCAL_CACHE_PREFERENCES"
private const val IS_FIRST_APP_RUN_KEY = "IS_FIRST_APP_RUN_KEY"
private const val INSTALLMENT_ID_KEY = "INSTALLMENT_ID_KEY"

internal class LocalCache(private val context: Context) {

    fun isFirstAppRun(isFirstAppRun: Boolean) {
        getSettingsPreferences().edit().putBoolean(IS_FIRST_APP_RUN_KEY, isFirstAppRun).apply()
    }

    fun isFirstAppRun(): Boolean {
        return getSettingsPreferences().getBoolean(IS_FIRST_APP_RUN_KEY, true)
    }

    fun saveInstallmentID(installmentID: String) {
        getSettingsPreferences().edit().putString(INSTALLMENT_ID_KEY, installmentID).apply()
    }

    fun getInstallmentID(): String? {
        return getSettingsPreferences().getString(INSTALLMENT_ID_KEY, null)
    }

    private fun getSettingsPreferences(): SharedPreferences {
        return context.getSharedPreferences(LOCAL_CACHE_PREFERENCES, Context.MODE_PRIVATE)
    }
}