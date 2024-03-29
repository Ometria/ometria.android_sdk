package com.android.ometriasdk.core

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.network.toJson
import com.android.ometriasdk.core.network.toOmetriaEventList

private const val LOCAL_CACHE_PREFERENCES = "LOCAL_CACHE_PREFERENCES"
private const val LOCAL_ENCRYPTED_CACHE_PREFERENCES = "LOCAL_ENCRYPTED_CACHE_PREFERENCES"
private const val IS_FIRST_APP_RUN_KEY = "IS_FIRST_APP_RUN_KEY"
private const val INSTALLATION_ID_KEY = "INSTALLATION_ID_KEY"
private const val EVENTS_KEY = "EVENTS_KEY"
private const val PUSH_TOKEN_KEY = "PUSH_TOKEN_KEY"
private const val CUSTOMER_ID_KEY = "CUSTOMER_ID_KEY"
private const val EMAIL_KEY = "EMAIL_KEY"
private const val ARE_NOTIFICATIONS_ENABLED_KEY = "ARE_NOTIFICATIONS_ENABLED_KEY"
private const val IS_FIRST_PERMISSION_UPDATE_EVENT_KEY = "IS_FIRST_PERMISSION_UPDATE_EVENT_KEY"
private const val JSON_ARRAY = "[]"
private const val SDK_VERSION_RN_KEY = "SDK_VERSION_RN_KEY"
private const val API_TOKEN_KEY = "API_TOKEN_KEY"

internal class LocalCache(private val context: Context) {

    private val masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    private var localCacheEncryptedPreferences: SharedPreferences

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

    private fun getLocalEncryptedCachePreferences(): SharedPreferences {
        return EncryptedSharedPreferences.create(
            LOCAL_ENCRYPTED_CACHE_PREFERENCES,
            masterKey,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveIsFirstAppRun(isFirstAppRun: Boolean) {
        localCacheEncryptedPreferences.edit().putBoolean(IS_FIRST_APP_RUN_KEY, isFirstAppRun)
            .apply()
    }

    fun isFirstAppRun(): Boolean {
        return localCacheEncryptedPreferences.getBoolean(IS_FIRST_APP_RUN_KEY, true)
    }

    fun saveInstallationId(installationId: String?) {
        localCacheEncryptedPreferences.edit().putString(INSTALLATION_ID_KEY, installationId).apply()
    }

    fun getInstallationId(): String? {
        return localCacheEncryptedPreferences.getString(INSTALLATION_ID_KEY, null)
    }

    fun saveEvent(ometriaEvent: OmetriaEvent) {
        val eventsString =
            localCacheEncryptedPreferences.getString(EVENTS_KEY, JSON_ARRAY) ?: JSON_ARRAY

        val eventsList = eventsString.toOmetriaEventList()

        eventsList.add(ometriaEvent)

        localCacheEncryptedPreferences.edit().putString(EVENTS_KEY, eventsList.toJson().toString())
            .apply()
    }

    fun getEvents(): List<OmetriaEvent> {
        val eventsString = localCacheEncryptedPreferences.getString(EVENTS_KEY, null) ?: JSON_ARRAY

        return eventsString.toOmetriaEventList()
    }

    fun updateEvents(events: List<OmetriaEvent>?, isBeingFlushed: Boolean) {
        events ?: return

        val cachedEvents = getEvents()

        events.forEach { event ->
            cachedEvents.firstOrNull { it.eventId == event.eventId }?.isBeingFlushed =
                isBeingFlushed
        }

        localCacheEncryptedPreferences.edit()
            .putString(EVENTS_KEY, cachedEvents.toJson().toString())
            .apply()
    }

    fun removeEvents(eventsToRemove: List<OmetriaEvent>) {
        val eventsList = getEvents().toMutableList()

        eventsToRemove.forEach { event ->
            eventsList.remove(eventsList.firstOrNull { it.eventId == event.eventId })
        }

        localCacheEncryptedPreferences.edit().putString(EVENTS_KEY, eventsList.toJson().toString())
            .apply()
    }

    fun savePushToken(pushToken: String) {
        localCacheEncryptedPreferences.edit().putString(PUSH_TOKEN_KEY, pushToken).apply()
    }

    fun getPushToken(): String? {
        return localCacheEncryptedPreferences.getString(PUSH_TOKEN_KEY, null)
    }

    fun clearEvents() {
        localCacheEncryptedPreferences.edit().remove(EVENTS_KEY).apply()
    }

    fun saveCustomerId(customerId: String?) {
        localCacheEncryptedPreferences.edit().putString(CUSTOMER_ID_KEY, customerId).apply()
    }

    fun getCustomerId(): String? {
        return localCacheEncryptedPreferences.getString(CUSTOMER_ID_KEY, null)
    }

    fun saveEmail(email: String?) {
        localCacheEncryptedPreferences.edit().putString(EMAIL_KEY, email).apply()
    }

    fun getEmail(): String? {
        return localCacheEncryptedPreferences.getString(EMAIL_KEY, null)
    }

    fun clearProfileIdentifiedData() {
        localCacheEncryptedPreferences.edit().remove(CUSTOMER_ID_KEY).apply()
        localCacheEncryptedPreferences.edit().remove(EMAIL_KEY).apply()
    }

    fun saveAreNotificationsEnabled(areNotificationsEnabled: Boolean) {
        localCacheEncryptedPreferences.edit()
            .putBoolean(ARE_NOTIFICATIONS_ENABLED_KEY, areNotificationsEnabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return localCacheEncryptedPreferences.getBoolean(ARE_NOTIFICATIONS_ENABLED_KEY, true)
    }

    fun saveIsFirstPermissionsUpdateEvent(isFirstPermissionsUpdateEvent: Boolean) {
        localCacheEncryptedPreferences.edit()
            .putBoolean(IS_FIRST_PERMISSION_UPDATE_EVENT_KEY, isFirstPermissionsUpdateEvent).apply()
    }

    fun isFirstPermissionsUpdateEvent(): Boolean {
        return localCacheEncryptedPreferences.getBoolean(IS_FIRST_PERMISSION_UPDATE_EVENT_KEY, true)
    }

    fun getSdkVersionRN(): String? {
        return localCacheEncryptedPreferences.getString(SDK_VERSION_RN_KEY, null)
    }

    fun saveApiToken(apiToken: String) {
        localCacheEncryptedPreferences.edit().putString(API_TOKEN_KEY, apiToken).apply()
    }

    fun getApiToken(): String? {
        return localCacheEncryptedPreferences.getString(API_TOKEN_KEY, null)
    }
}