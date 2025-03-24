package com.android.ometriasdk.core

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.network.toOmetriaEventList

private const val LOCAL_CACHE_PREFERENCES = "LOCAL_CACHE_PREFERENCES"
private const val LOCAL_ENCRYPTED_CACHE_PREFERENCES = "LOCAL_ENCRYPTED_CACHE_PREFERENCES"
private const val JSON_ARRAY = "[]"
private const val IS_FIRST_APP_RUN_KEY = "IS_FIRST_APP_RUN_KEY"
private const val INSTALLATION_ID_KEY = "INSTALLATION_ID_KEY"
private const val EVENTS_KEY = "EVENTS_KEY"
private const val PUSH_TOKEN_KEY = "PUSH_TOKEN_KEY"
private const val CUSTOMER_ID_KEY = "CUSTOMER_ID_KEY"
private const val EMAIL_KEY = "EMAIL_KEY"
private const val STORE_ID_KEY = "STORE_ID_KEY"
private const val ARE_NOTIFICATIONS_ENABLED_KEY = "ARE_NOTIFICATIONS_ENABLED_KEY"
private const val IS_FIRST_PERMISSION_UPDATE_EVENT_KEY = "IS_FIRST_PERMISSION_UPDATE_EVENT_KEY"
private const val SDK_VERSION_RN_KEY = "SDK_VERSION_RN_KEY"
private const val API_TOKEN_KEY = "API_TOKEN_KEY"
private const val LAST_PUSH_TOKEN_REFRESH_TIMESTAMP_KEY = "LAST_PUSH_TOKEN_REFRESH_TIMESTAMP_KEY"

internal class LocalCache(private val context: Context) {
    private var localCacheEncryptedPreferences: SharedPreferences
    private var createSecurePreferencesAttempts: Int = 0

    init {
        val localCachePreferences = getLocalCachePreferences()
        localCacheEncryptedPreferences = getLocalEncryptedCachePreferences()

        if (localCachePreferences.all.isNotEmpty()) {
            localCachePreferences.copyTo(localCacheEncryptedPreferences)
            localCachePreferences.clear()
        }
    }

    private fun getLocalCachePreferences(): SharedPreferences {
        return context.getSharedPreferences(LOCAL_CACHE_PREFERENCES, Context.MODE_PRIVATE)
    }

    private fun getLocalEncryptedCachePreferences(): SharedPreferences = try {
        createSecurePreferencesAttempts++
        val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            LOCAL_ENCRYPTED_CACHE_PREFERENCES,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        if (createSecurePreferencesAttempts > 1) throw e

        context.getSharedPreferences(
            LOCAL_ENCRYPTED_CACHE_PREFERENCES,
            Context.MODE_PRIVATE
        ).clear()

        getLocalEncryptedCachePreferences()
    }

    fun isCacheEmpty(): Boolean = localCacheEncryptedPreferences.all.isEmpty()

    fun clearAllCache() = localCacheEncryptedPreferences.clear()

    fun isFirstAppRun(): Boolean {
        return localCacheEncryptedPreferences.getBoolean(IS_FIRST_APP_RUN_KEY, true)
    }

    fun getInstallationId(): String? {
        return localCacheEncryptedPreferences.getString(INSTALLATION_ID_KEY, null)
    }

    fun getEvents(): List<OmetriaEvent> {
        val eventsString = localCacheEncryptedPreferences
            .getString(EVENTS_KEY, null) ?: JSON_ARRAY

        return eventsString.toOmetriaEventList()
    }

    fun getPushToken(): String? {
        return localCacheEncryptedPreferences.getString(PUSH_TOKEN_KEY, null)
    }

    fun getCustomerId(): String? {
        return localCacheEncryptedPreferences.getString(CUSTOMER_ID_KEY, null)
    }

    fun getEmail(): String? {
        return localCacheEncryptedPreferences.getString(EMAIL_KEY, null)
    }

    fun getStoreId(): String? {
        return localCacheEncryptedPreferences.getString(STORE_ID_KEY, null)
    }

    fun areNotificationsEnabled(): Boolean {
        return localCacheEncryptedPreferences.getBoolean(ARE_NOTIFICATIONS_ENABLED_KEY, true)
    }

    fun isFirstPermissionsUpdateEvent(): Boolean {
        return localCacheEncryptedPreferences.getBoolean(
            IS_FIRST_PERMISSION_UPDATE_EVENT_KEY,
            true
        )
    }

    fun getSdkVersionRN(): String? {
        return localCacheEncryptedPreferences.getString(SDK_VERSION_RN_KEY, null)
    }

    fun getApiToken(): String? {
        return localCacheEncryptedPreferences.getString(API_TOKEN_KEY, null)
    }

    fun getLastPushTokenRefreshTimestamp(): Long {
        return localCacheEncryptedPreferences.getLong(LAST_PUSH_TOKEN_REFRESH_TIMESTAMP_KEY, 0)
    }
}
