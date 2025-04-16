package com.android.ometriasdk.core

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.network.toJson
import com.android.ometriasdk.core.network.toOmetriaEventList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private const val LOCAL_CACHE_DATA_STORE_PREFERENCES = "LOCAL_CACHE_DATA_STORE_PREFERENCES"
private const val JSON_ARRAY = "[]"

internal class LocalCacheDataStore private constructor(private val context: Context) {
    private val Context.localCacheDataStore: DataStore<Preferences> by preferencesDataStore(name = LOCAL_CACHE_DATA_STORE_PREFERENCES)

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: LocalCacheDataStore? = null

        fun getInstance(context: Context): LocalCacheDataStore {
            if (instance == null) {
                instance = LocalCacheDataStore(context)
            }
            return instance!!
        }
    }

    init {
        val oldCache = LocalCache(context)
        if (oldCache.isCacheEmpty().not()) {
            copyDataFromOldCache(oldCache)
        }
    }

    private fun copyDataFromOldCache(oldCache: LocalCache) = runBlocking {
        context.localCacheDataStore.edit { preferences: MutablePreferences ->
            preferences[PreferencesKeys.isFirstAppRun] = oldCache.isFirstAppRun()
            preferences[PreferencesKeys.events] = oldCache.getEvents().toJson().toString()
            preferences[PreferencesKeys.areNotificationsEnabled] = oldCache.areNotificationsEnabled()
            preferences[PreferencesKeys.isFirstPermissionUpdateEvent] = oldCache.isFirstPermissionsUpdateEvent()
            preferences[PreferencesKeys.lastPushTokenRefreshTimestamp] = oldCache.getLastPushTokenRefreshTimestamp()

            oldCache.getInstallationId()?.let { preferences[PreferencesKeys.installationId] = it }
            oldCache.getPushToken()?.let { preferences[PreferencesKeys.pushToken] = it }
            oldCache.getCustomerId()?.let { preferences[PreferencesKeys.customerId] = it }
            oldCache.getEmail()?.let { preferences[PreferencesKeys.email] = it }
            oldCache.getStoreId()?.let { preferences[PreferencesKeys.storeId] = it }
            oldCache.getSdkVersionRN()?.let { preferences[PreferencesKeys.sdkVersionRN] = it }
            oldCache.getApiToken()?.let { preferences[PreferencesKeys.apiToken] = it }

            oldCache.clearAllCache()
        }
    }

    suspend fun saveIsFirstAppRun(isFirstAppRun: Boolean) {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                preferences[PreferencesKeys.isFirstAppRun] = isFirstAppRun
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to save isFirstAppRun")
        }
    }

    fun isFirstAppRun(): Flow<Boolean> = context.localCacheDataStore.data.map { preferences: Preferences ->
        preferences[PreferencesKeys.isFirstAppRun] != false
    }

    suspend fun saveInstallationId(installationId: String?) {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                if (installationId == null) {
                    preferences.remove(PreferencesKeys.installationId)
                    return@edit
                }
                preferences[PreferencesKeys.installationId] = installationId
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to save installationId")
        }
    }

    fun getInstallationId(): Flow<String?> = context.localCacheDataStore.data.map { preferences: Preferences ->
        preferences[PreferencesKeys.installationId]
    }

    suspend fun saveEvent(ometriaEvent: OmetriaEvent) {
        val eventsList = getEvents().firstOrNull().orEmpty().toMutableList()

        eventsList.add(ometriaEvent)

        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                preferences[PreferencesKeys.events] = eventsList.toJson().toString()
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to save event")
        }
    }

    fun getEvents(): Flow<List<OmetriaEvent>> = context.localCacheDataStore.data.map { preferences: Preferences ->
        (preferences[PreferencesKeys.events] ?: JSON_ARRAY).toOmetriaEventList()
    }

    suspend fun updateEvents(events: List<OmetriaEvent>?, isBeingFlushed: Boolean) {
        events ?: return

        val cachedEvents = getEvents().firstOrNull().orEmpty()
        events.forEach { event ->
            cachedEvents.firstOrNull { it.eventId == event.eventId }?.isBeingFlushed = isBeingFlushed
        }

        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                preferences[PreferencesKeys.events] = cachedEvents.toJson().toString()
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to update events")
        }
    }

    suspend fun removeEvents(eventsToRemove: List<OmetriaEvent>) {
        val eventsList = getEvents().firstOrNull().orEmpty().toMutableList()

        eventsToRemove.forEach { event ->
            eventsList.remove(eventsList.firstOrNull { it.eventId == event.eventId })
        }

        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                preferences[PreferencesKeys.events] = eventsList.toJson().toString()
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to remove events")
        }
    }

    suspend fun savePushToken(pushToken: String) {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                preferences[PreferencesKeys.pushToken] = pushToken
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to save pushToken")
        }
    }

    fun getPushToken(): Flow<String?> = context.localCacheDataStore.data.map { preferences: Preferences ->
        preferences[PreferencesKeys.pushToken]
    }

    suspend fun clearEvents() {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                preferences.remove(PreferencesKeys.events)
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to clear events")
        }
    }

    suspend fun saveCustomerId(customerId: String?) {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                if (customerId == null) {
                    preferences.remove(PreferencesKeys.customerId)
                    return@edit
                }
                preferences[PreferencesKeys.customerId] = customerId
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to save customerId")
        }
    }

    fun getCustomerId(): Flow<String?> = context.localCacheDataStore.data.map { preferences: Preferences ->
        preferences[PreferencesKeys.customerId]
    }

    suspend fun saveEmail(email: String?) {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                if (email == null) {
                    preferences.remove(PreferencesKeys.email)
                    return@edit
                }
                preferences[PreferencesKeys.email] = email
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to save email")
        }
    }

    fun getEmail(): Flow<String?> = context.localCacheDataStore.data.map { preferences: Preferences ->
        preferences[PreferencesKeys.email]
    }

    suspend fun saveStoreId(storeId: String?) {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                if (storeId == null) {
                    preferences.remove(PreferencesKeys.storeId)
                    return@edit
                }
                preferences[PreferencesKeys.storeId] = storeId
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to save storeId")
        }
    }

    fun getStoreId(): Flow<String?> = context.localCacheDataStore.data.map { preferences: Preferences ->
        preferences[PreferencesKeys.storeId]
    }

    suspend fun clearProfileIdentifiedData() {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                preferences.remove(PreferencesKeys.customerId)
                preferences.remove(PreferencesKeys.email)
                preferences.remove(PreferencesKeys.storeId)
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to clear profile identified data")
        }
    }

    suspend fun saveAreNotificationsEnabled(areNotificationsEnabled: Boolean) {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                preferences[PreferencesKeys.areNotificationsEnabled] = areNotificationsEnabled
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to save areNotificationsEnabled")
        }
    }

    fun areNotificationsEnabled(): Flow<Boolean> = context.localCacheDataStore.data.map { preferences: Preferences ->
        preferences[PreferencesKeys.areNotificationsEnabled] != false
    }

    suspend fun saveIsFirstPermissionsUpdateEvent(isFirstPermissionsUpdateEvent: Boolean) {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                preferences[PreferencesKeys.isFirstPermissionUpdateEvent] = isFirstPermissionsUpdateEvent
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to save isFirstPermissionsUpdateEvent")
        }
    }

    fun isFirstPermissionsUpdateEvent(): Flow<Boolean> = context.localCacheDataStore.data.map { preferences: Preferences ->
        preferences[PreferencesKeys.isFirstPermissionUpdateEvent] != false
    }

    fun getSdkVersionRN(): Flow<String?> = context.localCacheDataStore.data.map { preferences: Preferences ->
        preferences[PreferencesKeys.sdkVersionRN]
    }

    suspend fun saveApiToken(apiToken: String) {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                preferences[PreferencesKeys.apiToken] = apiToken
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to save apiToken")
        }
    }

    fun getApiToken(): Flow<String?> = context.localCacheDataStore.data.map { preferences: Preferences ->
        preferences[PreferencesKeys.apiToken]
    }

    suspend fun saveLastPushTokenRefreshTimestamp(timestamp: Long) {
        try {
            context.localCacheDataStore.edit { preferences: MutablePreferences ->
                preferences[PreferencesKeys.lastPushTokenRefreshTimestamp] = timestamp
            }
        } catch (e: Exception) {
            Logger.e(Constants.Logger.CACHE, e.message ?: "Failed to save apiToken")
        }
    }

    fun getLastPushTokenRefreshTimestamp(): Flow<Long> = context.localCacheDataStore.data.map { preferences: Preferences ->
        preferences[PreferencesKeys.lastPushTokenRefreshTimestamp] ?: 0L
    }

    private object PreferencesKeys {
        val isFirstAppRun: Preferences.Key<Boolean> = booleanPreferencesKey(name = "IS_FIRST_APP_RUN_KEY")
        val installationId: Preferences.Key<String> = stringPreferencesKey(name = "INSTALLATION_ID_KEY")
        val events: Preferences.Key<String> = stringPreferencesKey(name = "EVENTS_KEY")
        val pushToken: Preferences.Key<String> = stringPreferencesKey(name = "PUSH_TOKEN_KEY")
        val customerId: Preferences.Key<String> = stringPreferencesKey(name = "CUSTOMER_ID_KEY")
        val email: Preferences.Key<String> = stringPreferencesKey(name = "EMAIL_KEY")
        val storeId: Preferences.Key<String> = stringPreferencesKey(name = "STORE_ID_KEY")
        val areNotificationsEnabled: Preferences.Key<Boolean> = booleanPreferencesKey(name = "ARE_NOTIFICATIONS_ENABLED_KEY")
        val isFirstPermissionUpdateEvent: Preferences.Key<Boolean> = booleanPreferencesKey(name = "IS_FIRST_PERMISSION_UPDATE_EVENT_KEY")
        val sdkVersionRN: Preferences.Key<String> = stringPreferencesKey(name = "SDK_VERSION_RN_KEY")
        val apiToken: Preferences.Key<String> = stringPreferencesKey(name = "API_TOKEN_KEY")
        val lastPushTokenRefreshTimestamp: Preferences.Key<Long> = longPreferencesKey(name = "LAST_PUSH_TOKEN_REFRESH_TIMESTAMP_KEY")
    }
}