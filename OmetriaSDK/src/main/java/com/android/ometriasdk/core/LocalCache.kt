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

    fun saveEvent(cachedEvent: OmetriaEvent) {
        val eventsSet: MutableSet<String> = getLocalCachePreferences()
            .getStringSet(EVENTS_KEY, null) ?: mutableSetOf()

        eventsSet.add(AppGson.instance.toJson(cachedEvent))

        getLocalCachePreferences().edit().remove(EVENTS_KEY).apply()
        getLocalCachePreferences().edit().putStringSet(EVENTS_KEY, eventsSet).apply()
    }

    fun getEvents(): Set<String>? {
        return getLocalCachePreferences().getStringSet(EVENTS_KEY, null)
    }

    fun removeEvents(events: List<OmetriaEvent>) {
        val eventsSet: MutableSet<String> = getLocalCachePreferences()
            .getStringSet(EVENTS_KEY, null) ?: mutableSetOf()
        val eventsList = eventsSet.sorted().toMutableList()

        for (i in events.indices) {
            eventsList.removeAt(i)
        }

        getLocalCachePreferences().edit().remove(EVENTS_KEY).apply()
        getLocalCachePreferences().edit().putStringSet(EVENTS_KEY, eventsList.toSet()).apply()
    }

    private fun getLocalCachePreferences(): SharedPreferences {
        return context.getSharedPreferences(LOCAL_CACHE_PREFERENCES, Context.MODE_PRIVATE)
    }
}