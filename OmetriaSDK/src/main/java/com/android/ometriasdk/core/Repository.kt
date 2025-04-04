package com.android.ometriasdk.core

import android.os.Handler
import android.os.Looper
import com.android.ometriasdk.core.Constants.Logger.NETWORK
import com.android.ometriasdk.core.Constants.Params.CUSTOMER_ID
import com.android.ometriasdk.core.Constants.Params.EMAIL
import com.android.ometriasdk.core.Constants.Params.STORE_ID
import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.event.toApiRequest
import com.android.ometriasdk.core.listener.ProcessAppLinkListener
import com.android.ometriasdk.core.network.Client
import com.android.ometriasdk.core.network.OmetriaThreadPoolExecutor
import com.android.ometriasdk.core.network.RedirectService
import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import java.io.IOException
import java.net.MalformedURLException

private const val TOO_MANY_REQUESTS_STATUS_CODE = 429

internal class Repository(
    private val client: Client,
    private val localCache: LocalCacheDataStore,
    private val executor: OmetriaThreadPoolExecutor
) {

    private val resultHandler: Handler = Handler(Looper.getMainLooper())
    private val dropStatusCodesRange = 400..499

    val isFirstAppRun: Boolean
        get() = runBlocking { localCache.isFirstAppRun().firstOrNull() != false }
    val installationId: String?
        get() = runBlocking { localCache.getInstallationId().firstOrNull() }
    val events: List<OmetriaEvent>
        get() = runBlocking { localCache.getEvents().firstOrNull() ?: emptyList() }
    val pushToken: String?
        get() = runBlocking { localCache.getPushToken().firstOrNull() }
    val customerId: String?
        get() = runBlocking { localCache.getCustomerId().firstOrNull() }
    val email: String?
        get() = runBlocking { localCache.getEmail().firstOrNull() }
    val storeId: String?
        get() = runBlocking { localCache.getStoreId().firstOrNull() }
    val areNotificationsEnabled: Boolean
        get() = runBlocking { localCache.areNotificationsEnabled().firstOrNull() != false }
    val isFirstPermissionsUpdateEvent: Boolean
        get() = runBlocking { localCache.isFirstPermissionsUpdateEvent().firstOrNull() != false }
    val sdkVersionRN: String?
        get() = runBlocking { localCache.getSdkVersionRN().firstOrNull() }
    val lastPushTokenRefreshTimestamp: Long
        get() = runBlocking { localCache.getLastPushTokenRefreshTimestamp().firstOrNull() ?: 0 }
    val apiToken: String?
        get() = runBlocking { localCache.getApiToken().firstOrNull() }

    fun flushEvents(events: List<OmetriaEvent>, success: () -> Unit, error: () -> Unit) {
        events.forEach { it.isBeingFlushed = true }
        val apiRequest = events.toApiRequest()
        updateEvents(events, true)
        postEvents(apiRequest, success, error)
    }

    private fun postEvents(
        apiRequest: OmetriaApiRequest,
        success: () -> Unit,
        error: () -> Unit
    ) = executor.execute {
        try {
            client.postEvents(
                ometriaApiRequest = apiRequest,
                success = {
                    success()
                    removeEvents(apiRequest.events)
                },
                error = {
                    Logger.e(NETWORK, it.detail ?: "Unknown error")
                    error()

                    if (it.status in (dropStatusCodesRange).minus(TOO_MANY_REQUESTS_STATUS_CODE)) {
                        removeEvents(apiRequest.events)
                    } else {
                        updateEvents(apiRequest.events, false)
                    }
                }
            )
        } catch (e: IOException) {
            Logger.e(NETWORK, e.message, e)
            error()
        }
    }

    fun saveIsFirstAppRun(isFirstAppRun: Boolean) = runBlocking { localCache.saveIsFirstAppRun(isFirstAppRun) }

    fun saveInstallationId(installationId: String) = runBlocking { localCache.saveInstallationId(installationId) }

    fun saveEvent(ometriaEvent: OmetriaEvent) = runBlocking { localCache.saveEvent(ometriaEvent) }

    private fun updateEvents(events: List<OmetriaEvent>?, isBeingFlushed: Boolean) = runBlocking {
        localCache.updateEvents(events, isBeingFlushed)
    }

    private fun removeEvents(events: List<OmetriaEvent>?) {
        events ?: return
        runBlocking { localCache.removeEvents(events) }
    }

    fun savePushToken(pushToken: String) = runBlocking { localCache.savePushToken(pushToken) }

    fun saveCustomerId(customerId: String) = runBlocking { localCache.saveCustomerId(customerId) }

    fun saveEmail(email: String) = runBlocking { localCache.saveEmail(email) }

    fun saveStoreId(storeId: String?) = runBlocking { localCache.saveStoreId(storeId) }

    fun cacheProfileIdentifiedData(data: Map<String, Any>?) {
        data?.let {
            it[CUSTOMER_ID]?.let { customerId -> saveCustomerId(customerId as String) }
            it[EMAIL]?.let { email -> saveEmail(email as String) }
            it[STORE_ID]?.let { storeId -> saveStoreId(storeId as String) }
        }
    }

    fun clearProfileIdentifiedData() = runBlocking { localCache.clearProfileIdentifiedData() }

    fun saveAreNotificationsEnabled(areNotificationsEnabled: Boolean) = runBlocking {
        localCache.saveAreNotificationsEnabled(areNotificationsEnabled)
        localCache.saveIsFirstPermissionsUpdateEvent(false)
    }

    fun saveLastPushTokenRefreshTimestamp(timestamp: Long) = runBlocking { localCache.saveLastPushTokenRefreshTimestamp(timestamp) }

    fun saveApiToken(apiToken: String) = runBlocking { localCache.saveApiToken(apiToken) }

    fun clearEvents() = runBlocking { localCache.clearEvents() }

    fun getRedirectForUrl(
        url: String,
        listener: ProcessAppLinkListener,
        domain: String? = null,
        regex: Regex? = null
    ) {
        executor.execute {
            val finalUrl: String
            try {
                finalUrl = RedirectService.getFinalRedirectUrl(url = url, domain = domain, regex = regex)
            } catch (e: MalformedURLException) {
                resultHandler.post { listener.onProcessFailed(e.message ?: "Something went wrong") }
                return@execute
            } catch (e: IOException) {
                resultHandler.post { listener.onProcessFailed(e.message ?: "Something went wrong") }
                return@execute
            }
            resultHandler.post { listener.onProcessResult(finalUrl.toString()) }
        }
    }
}
