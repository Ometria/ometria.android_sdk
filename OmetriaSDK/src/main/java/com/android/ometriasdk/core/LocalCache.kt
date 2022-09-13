package com.android.ometriasdk.core

import android.content.Context
import android.content.SharedPreferences
import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.network.toJson
import com.android.ometriasdk.core.network.toOmetriaEventList

private const val LOCAL_CACHE_PREFERENCES = "LOCAL_CACHE_PREFERENCES"
private const val IS_FIRST_APP_RUN_KEY = "IS_FIRST_APP_RUN_KEY"
private const val INSTALLATION_ID_KEY = "INSTALLATION_ID_KEY"
private const val EVENTS_KEY = "EVENTS_KEY"
private const val PUSH_TOKEN_KEY = "PUSH_TOKEN_KEY"
private const val CUSTOMER_ID_KEY = "CUSTOMER_ID_KEY"
private const val EMAIL_KEY = "EMAIL_KEY"
private const val ARE_NOTIFICATIONS_ENABLED_KEY = "ARE_NOTIFICATIONS_ENABLED_KEY"
private const val JSON_ARRAY = "[]"
private const val SDK_VERSION_RN_KEY = "SDK_VERSION_RN_KEY"

internal class LocalCache(private val context: Context) {

    private fun getLocalCachePreferences(): SharedPreferences {
        return context.getSharedPreferences(LOCAL_CACHE_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun saveIsFirstAppRun(isFirstAppRun: Boolean) {
        getLocalCachePreferences().edit().putBoolean(IS_FIRST_APP_RUN_KEY, isFirstAppRun).apply()
    }

    fun isFirstAppRun(): Boolean {
        return getLocalCachePreferences().getBoolean(IS_FIRST_APP_RUN_KEY, true)
    }

    fun saveInstallationId(installationId: String?) {
        getLocalCachePreferences().edit().putString(INSTALLATION_ID_KEY, installationId).apply()
    }

    fun getInstallationId(): String? {
        return getLocalCachePreferences().getString(INSTALLATION_ID_KEY, null)
    }

    fun saveEvent(ometriaEvent: OmetriaEvent) {
        val eventsString =
            getLocalCachePreferences().getString(EVENTS_KEY, JSON_ARRAY) ?: JSON_ARRAY

        val eventsList = eventsString.toOmetriaEventList()

        eventsList.add(ometriaEvent)

        getLocalCachePreferences().edit().putString(EVENTS_KEY, eventsList.toJson().toString())
            .apply()
    }

    fun getEvents(): List<OmetriaEvent> {
        val eventsString = getLocalCachePreferences().getString(EVENTS_KEY, null) ?: JSON_ARRAY

        return eventsString.toOmetriaEventList()
    }

    fun updateEvents(events: List<OmetriaEvent>?, isBeingFlushed: Boolean) {
        events ?: return

        val cachedEvents = getEvents()

        events.forEach { event ->
            cachedEvents.firstOrNull { it.eventId == event.eventId }?.isBeingFlushed =
                isBeingFlushed
        }

        getLocalCachePreferences().edit()
            .putString(EVENTS_KEY, cachedEvents.toJson().toString())
            .apply()
    }

    fun removeEvents(eventsToRemove: List<OmetriaEvent>) {
        val eventsList = getEvents().toMutableList()

        eventsToRemove.forEach { event ->
            eventsList.remove(eventsList.firstOrNull { it.eventId == event.eventId })
        }

        getLocalCachePreferences().edit().putString(EVENTS_KEY, eventsList.toJson().toString())
            .apply()
    }

    fun savePushToken(pushToken: String) {
        getLocalCachePreferences().edit().putString(PUSH_TOKEN_KEY, pushToken).apply()
    }

    fun getPushToken(): String? {
        return getLocalCachePreferences().getString(PUSH_TOKEN_KEY, null)
    }

    fun clearEvents() {
        getLocalCachePreferences().edit().remove(EVENTS_KEY).apply()
    }

    fun saveCustomerId(customerId: String?) {
        getLocalCachePreferences().edit().putString(CUSTOMER_ID_KEY, customerId).apply()
    }

    fun getCustomerId(): String? {
        return getLocalCachePreferences().getString(CUSTOMER_ID_KEY, null)
    }

    fun saveEmail(email: String?) {
        getLocalCachePreferences().edit().putString(EMAIL_KEY, email).apply()
    }

    fun getEmail(): String? {
        return getLocalCachePreferences().getString(EMAIL_KEY, null)
    }

    fun clearProfileIdentifiedData() {
        getLocalCachePreferences().edit().remove(CUSTOMER_ID_KEY).apply()
        getLocalCachePreferences().edit().remove(EMAIL_KEY).apply()
    }

    fun saveAreNotificationsEnabled(areNotificationsEnabled: Boolean) {
        getLocalCachePreferences().edit()
            .putBoolean(ARE_NOTIFICATIONS_ENABLED_KEY, areNotificationsEnabled).apply()
    }

    fun areNotificationsEnabled(): Boolean {
        return getLocalCachePreferences().getBoolean(ARE_NOTIFICATIONS_ENABLED_KEY, true)
    }

    fun getSdkVersionRN(): String? {
        return getLocalCachePreferences().getString(SDK_VERSION_RN_KEY, null)
    }
}