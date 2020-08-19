package com.android.ometriasdk.core

import android.content.Context
import android.content.SharedPreferences
import com.android.ometriasdk.core.event.OmetriaEvent

/**
 * Created by cristiandregan
 * on 24/07/2020.
 */

private const val LOCAL_CACHE_PREFERENCES = "LOCAL_CACHE_PREFERENCES"
private const val IS_FIRST_APP_RUN_KEY = "IS_FIRST_APP_RUN_KEY"
private const val INSTALLATION_ID_KEY = "INSTALLATION_ID_KEY"
private const val EVENTS_KEY = "EVENTS_KEY"

internal class LocalCache(private val context: Context) {

    fun saveIsFirstAppRun(isFirstAppRun: Boolean) {
        getLocalCachePreferences().edit().putBoolean(IS_FIRST_APP_RUN_KEY, isFirstAppRun).apply()
    }

    fun isFirstAppRun(): Boolean {
        return getLocalCachePreferences().getBoolean(IS_FIRST_APP_RUN_KEY, true)
    }

    fun saveInstallationId(installationId: String) {
        getLocalCachePreferences().edit().putString(INSTALLATION_ID_KEY, installationId).apply()
    }

    fun getInstallationId(): String? {
        return getLocalCachePreferences().getString(INSTALLATION_ID_KEY, null)
    }

    fun saveEvent(ometriaEvent: OmetriaEvent) {
        val eventsString: String? = getLocalCachePreferences()
            .getString(EVENTS_KEY, "[]")

        val eventsList =
            AppGson.instance.fromJson(eventsString, Array<OmetriaEvent>::class.java).toMutableList()

        eventsList.add(ometriaEvent)

        getLocalCachePreferences().edit().putString(EVENTS_KEY, AppGson.instance.toJson(eventsList))
            .apply()
    }

    fun getEvents(): List<OmetriaEvent> {
        val eventsString = getLocalCachePreferences().getString(EVENTS_KEY, null) ?: ""

        return AppGson.instance.fromJson(eventsString, Array<OmetriaEvent>::class.java).toList()
    }

    fun updateEvents(events: List<OmetriaEvent>) {
        val cachedEvents = getEvents().toMutableList()

        events.forEach {
            cachedEvents[cachedEvents.indexOf(it)].isBeingFlushed = true
        }

        getLocalCachePreferences().edit().remove(EVENTS_KEY).apply()
        getLocalCachePreferences().edit()
            .putString(EVENTS_KEY, AppGson.instance.toJson(cachedEvents))
            .apply()
    }

    fun removeEvents(eventsToRemove: List<OmetriaEvent>) {
        val eventsList = getEvents().toMutableList()

        eventsToRemove.forEach { eventsList.remove(it) }

        getLocalCachePreferences().edit().remove(EVENTS_KEY).apply()
        getLocalCachePreferences().edit().putString(EVENTS_KEY, AppGson.instance.toJson(eventsList))
            .apply()
    }

    private fun getLocalCachePreferences(): SharedPreferences {
        return context.getSharedPreferences(LOCAL_CACHE_PREFERENCES, Context.MODE_PRIVATE)
    }
}