package com.android.ometriasdk.core

import com.android.ometriasdk.core.Constants.Logger.NETWORK
import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.event.toApiRequest
import com.android.ometriasdk.core.network.Client
import com.android.ometriasdk.core.network.OmetriaThreadPoolExecutor
import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import java.io.IOException

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

    fun isFirstAppRun(): Boolean {
        return localCache.isFirstAppRun()
    }

    fun saveInstallationId(installationId: String) {
        localCache.saveInstallationId(installationId)
    }

    fun getInstallationId(): String? {
        return localCache.getInstallationId()
    }

    fun saveEvent(ometriaEvent: OmetriaEvent) {
        localCache.saveEvent(ometriaEvent)
    }

    fun getEvents(): List<OmetriaEvent> {
        return localCache.getEvents()
    }

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

    fun getPushToken(): String? {
        return localCache.getPushToken()
    }

    fun saveCustomerId(customerId: String) {
        localCache.saveCustomerId(customerId)
    }

    fun getCustomerId(): String? {
        return localCache.getCustomerId()
    }

    fun saveEmail(email: String) {
        localCache.saveEmail(email)
    }

    fun getEmail(): String? {
        return localCache.getEmail()
    }

    fun clearProfileIdentifiedData() {
        localCache.clearProfileIdentifiedData()
    }
}