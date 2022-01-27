package com.android.ometriasdk.core

import android.os.Handler
import android.os.Looper
import com.android.ometriasdk.core.Constants.Logger.NETWORK
import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.event.toApiRequest
import com.android.ometriasdk.core.listener.ProcessAppLinkListener
import com.android.ometriasdk.core.network.Client
import com.android.ometriasdk.core.network.OmetriaThreadPoolExecutor
import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

/**
 * Created by cristiandregan
 * on 06/08/2020.
 */

private const val TOO_MANY_REQUESTS_STATUS_CODE = 429

internal class Repository(
    private val client: Client,
    private val localCache: LocalCache,
    private val executor: OmetriaThreadPoolExecutor
) {

    private val resultHandler: Handler = Handler(Looper.getMainLooper())
    private val dropStatusCodesRange = 400..499

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
        localCache.saveIsFirstAppRun(isFirstAppRun)
    }

    fun isFirstAppRun(): Boolean = localCache.isFirstAppRun()

    fun saveInstallationId(installationId: String) {
        localCache.saveInstallationId(installationId)
    }

    fun getInstallationId(): String? = localCache.getInstallationId()

    fun saveEvent(ometriaEvent: OmetriaEvent) {
        localCache.saveEvent(ometriaEvent)
    }

    fun getEvents(): List<OmetriaEvent> = localCache.getEvents()

    private fun updateEvents(events: List<OmetriaEvent>?, isBeingFlushed: Boolean) {
        localCache.updateEvents(events, isBeingFlushed)
    }

    private fun removeEvents(events: List<OmetriaEvent>?) {
        events ?: return

        localCache.removeEvents(events)
    }

    fun savePushToken(pushToken: String) {
        localCache.savePushToken(pushToken)
    }

    fun getPushToken(): String? = localCache.getPushToken()

    fun saveCustomerId(customerId: String) {
        localCache.saveCustomerId(customerId)
    }

    fun getCustomerId(): String? = localCache.getCustomerId()

    fun saveEmail(email: String) {
        localCache.saveEmail(email)
    }

    fun getEmail(): String? = localCache.getEmail()

    fun clearProfileIdentifiedData() {
        localCache.clearProfileIdentifiedData()
    }

    fun saveAreNotificationsEnabled(areNotificationsEnabled: Boolean) {
        localCache.saveAreNotificationsEnabled(areNotificationsEnabled)
    }

    fun areNotificationsEnabled(): Boolean = localCache.areNotificationsEnabled()

    fun getRedirectForUrl(url: String, listener: ProcessAppLinkListener) {
        executor.execute {
            var urlTemp: URL? = null
            var connection: HttpURLConnection? = null
            try {
                urlTemp = URL(url)
            } catch (e: MalformedURLException) {
                listener.onProcessFailed(e.message ?: "Something went wrong")
            }
            try {
                connection = urlTemp?.openConnection() as HttpURLConnection
            } catch (e: IOException) {
                listener.onProcessFailed(e.message ?: "Something went wrong")
            }
            try {
                connection?.responseCode
            } catch (e: IOException) {
                listener.onProcessFailed(e.message ?: "Something went wrong")
            }
            resultHandler.post { listener.onProcessResult(connection?.url.toString()) }
            connection?.disconnect()
        }
    }

    fun getSdkVersionRN(): String? = localCache.getSdkVersionRN()
}