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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
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
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    var isFirstAppRun: Boolean = true
        private set
    var installationId: String? = null
        private set
    var events: List<OmetriaEvent> = emptyList()
        private set
    var pushToken: String? = null
        private set
    var customerId: String? = null
        private set
    var email: String? = null
        private set
    var storeId: String? = null
        private set
    var areNotificationsEnabled: Boolean = true
        private set
    var isFirstPermissionsUpdateEvent: Boolean = true
        private set
    var sdkVersionRN: String? = null
        private set
    var lastPushTokenRefreshTimestamp: Long = 0
        private set
    var apiToken: String? = null
        private set

    init {
        coroutineScope.launch {
            localCache.isFirstAppRun().collectLatest { isFirstAppRun = it }
        }
        coroutineScope.launch {
            localCache.getInstallationId().collectLatest { installationId = it }
        }
        coroutineScope.launch {
            localCache.getEvents().collectLatest { events = it }
        }
        coroutineScope.launch {
            localCache.getPushToken().collectLatest { pushToken = it }
        }
        coroutineScope.launch {
            localCache.getCustomerId().collectLatest { customerId = it }
        }
        coroutineScope.launch {
            localCache.getEmail().collectLatest { email = it }
        }
        coroutineScope.launch {
            localCache.getStoreId().collectLatest { storeId = it }
        }
        coroutineScope.launch {
            localCache.areNotificationsEnabled().collectLatest { areNotificationsEnabled = it }
        }
        coroutineScope.launch {
            localCache.isFirstPermissionsUpdateEvent().collectLatest { isFirstPermissionsUpdateEvent = it }
        }
        coroutineScope.launch {
            localCache.getSdkVersionRN().collectLatest { sdkVersionRN = it }
        }
        coroutineScope.launch {
            localCache.getLastPushTokenRefreshTimestamp().collectLatest { lastPushTokenRefreshTimestamp = it }
        }
        coroutineScope.launch {
            localCache.getApiToken().collectLatest { apiToken = it }
        }
    }

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
    ) {
        executor.execute {
            try {
                client.postEvents(apiRequest, success = {
                    success()
                    removeEvents(apiRequest.events)
                }, error = {
                    Logger.e(NETWORK, it.detail ?: "Unknown error")
                    error()

                    if (it.status in (dropStatusCodesRange).minus(TOO_MANY_REQUESTS_STATUS_CODE)) {
                        removeEvents(apiRequest.events)
                    } else {
                        updateEvents(apiRequest.events, false)
                    }
                })
            } catch (e: IOException) {
                Logger.e(NETWORK, e.message, e)
                error()
            }
        }
    }

    fun saveIsFirstAppRun(isFirstAppRun: Boolean) {
        coroutineScope.launch { localCache.saveIsFirstAppRun(isFirstAppRun) }
    }

    fun saveInstallationId(installationId: String) {
        coroutineScope.launch { localCache.saveInstallationId(installationId) }
    }

    fun saveEvent(ometriaEvent: OmetriaEvent) {
        coroutineScope.launch { localCache.saveEvent(ometriaEvent) }
    }

    private fun updateEvents(events: List<OmetriaEvent>?, isBeingFlushed: Boolean) {
        coroutineScope.launch { localCache.updateEvents(events, isBeingFlushed) }
    }

    private fun removeEvents(events: List<OmetriaEvent>?) {
        events ?: return

        coroutineScope.launch { localCache.removeEvents(events) }
    }

    fun savePushToken(pushToken: String) {
        coroutineScope.launch { localCache.savePushToken(pushToken) }
    }

    fun saveCustomerId(customerId: String) {
        coroutineScope.launch { localCache.saveCustomerId(customerId) }
    }

    fun saveEmail(email: String) {
        coroutineScope.launch { localCache.saveEmail(email) }
    }

    fun saveStoreId(storeId: String?) {
        coroutineScope.launch { localCache.saveStoreId(storeId) }
    }

    fun cacheProfileIdentifiedData(data: Map<String, Any>?) {
        data?.let {
            it[CUSTOMER_ID]?.let { customerId -> saveCustomerId(customerId as String) }
            it[EMAIL]?.let { email -> saveEmail(email as String) }
            it[STORE_ID]?.let { storeId -> saveStoreId(storeId as String) }
        }
    }

    fun clearProfileIdentifiedData() {
        coroutineScope.launch { localCache.clearProfileIdentifiedData() }
    }

    fun saveAreNotificationsEnabled(areNotificationsEnabled: Boolean) {
        coroutineScope.launch {
            localCache.saveAreNotificationsEnabled(areNotificationsEnabled)
            localCache.saveIsFirstPermissionsUpdateEvent(false)
        }
    }

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

    fun saveLastPushTokenRefreshTimestamp(timestamp: Long) {
        coroutineScope.launch { localCache.saveLastPushTokenRefreshTimestamp(timestamp) }
    }

    fun saveApiToken(apiToken: String) {
        coroutineScope.launch { localCache.saveApiToken(apiToken) }
    }

    fun clearEvents() {
        coroutineScope.launch { localCache.clearEvents() }
    }
}
