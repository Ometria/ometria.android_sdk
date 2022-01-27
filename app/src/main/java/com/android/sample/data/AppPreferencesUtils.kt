package com.android.sample.data

import android.content.Context
import android.content.SharedPreferences
import com.android.sample.SampleApp

private const val KEY_APP_PREFERENCES = "KEY_APP_PREFERENCES"
private const val KEY_API_TOKEN = "KEY_API_TOKEN"

object AppPreferencesUtils {

    private fun getAppPreferences(): SharedPreferences {
        return SampleApp.instance.applicationContext
            .getSharedPreferences(KEY_APP_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun saveApiToken(newToken: String) {
        getAppPreferences()
            .edit().putString(KEY_API_TOKEN, newToken).apply()
    }

    fun getApiToken(): String? {
        return getAppPreferences().getString(KEY_API_TOKEN, null)
    }
}