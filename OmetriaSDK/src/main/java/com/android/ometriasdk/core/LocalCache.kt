package com.android.ometriasdk.core

import android.content.Context
import android.content.SharedPreferences
import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.network.toJson
import com.android.ometriasdk.core.network.toOmetriaEventList
import org.json.JSONArray

/**
 * Created by cristiandregan
 * on 24/07/2020.
 */

private const val LOCAL_CACHE_PREFERENCES = "LOCAL_CACHE_PREFERENCES"
private const val IS_FIRST_APP_RUN_KEY = "IS_FIRST_APP_RUN_KEY"
private const val INSTALLATION_ID_KEY = "INSTALLATION_ID_KEY"
private const val EVENTS_KEY = "EVENTS_KEY"
private const val JSON_ARRAY = "[]"

internal class LocalCache(private val context: Context) {

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
        val eventsString = getLocalCachePreferences()
            .getString(EVENTS_KEY, JSON_ARRAY) ?: JSON_ARRAY

        val eventsList = eventsString.toOmetriaEventList()

        eventsList.add(ometriaEvent)

        getLocalCachePreferences().edit().putString(EVENTS_KEY, eventsList.toJson().toString())
            .apply()
    }

    fun getEvents(): List<OmetriaEvent> {
        val eventsString = getLocalCachePreferences().getString(EVENTS_KEY, null) ?: ""

        return eventsString.toOmetriaEventList()
    }

    fun updateEvents(events: List<OmetriaEvent>) {
        val cachedEvents = getEvents()

        events.forEach { event ->
            cachedEvents.first { it.eventId == event.eventId }.isBeingFlushed = true
        }

        getLocalCachePreferences().edit()
            .putString(EVENTS_KEY, cachedEvents.toJson().toString())
            .apply()
    }

    fun removeEvents(eventsToRemove: List<OmetriaEvent>) {
        val eventsList = getEvents().toMutableList()

        eventsToRemove.forEach { eventsList.remove(it) }

        getLocalCachePreferences().edit().putString(EVENTS_KEY, eventsList.toJson().toString())
            .apply()
    }

    private fun getLocalCachePreferences(): SharedPreferences {
        return context.getSharedPreferences(LOCAL_CACHE_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun clear() {
        getLocalCachePreferences().edit()
            .clear()
            .apply()
    }
}