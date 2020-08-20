package com.android.ometriasdk.core

import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.network.ApiCallback
import com.android.ometriasdk.core.network.Client
import com.android.ometriasdk.core.network.model.OmetriaApiError
import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import com.android.ometriasdk.core.network.model.OmetriaApiResponse

/**
 * Created by cristiandregan
 * on 06/08/2020.
 */

private val TAG = Repository::class.simpleName

internal class Repository(private val client: Client, private val localCache: LocalCache) {

    fun postEvents(apiRequest: OmetriaApiRequest) {
        Thread(Runnable {
            client.postEvents(apiRequest, object : ApiCallback<OmetriaApiResponse> {
                override fun onSuccess(response: OmetriaApiResponse?) {
                    Logger.d(
                        TAG, "Successfully flushed ${apiRequest.events?.size} events"
                    )

                    removeEvents(apiRequest.events)
                }

                override fun onError(ometriaApiError: OmetriaApiError?) {
                    Logger.e(TAG, ometriaApiError?.detail ?: "Unknown error")
                }
            })
        }).start()
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

    fun updateEvents(events: List<OmetriaEvent>) {
        localCache.updateEvents(events)
    }

    fun removeEvents(events: List<OmetriaEvent>?) {
        events ?: return

        localCache.removeEvents(events)
    }
}