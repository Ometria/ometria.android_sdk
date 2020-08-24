package com.android.ometriasdk.core

import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.event.toApiRequest
import com.android.ometriasdk.core.network.Client
import com.android.ometriasdk.core.network.OmetriaThreadPoolExecutor
import com.android.ometriasdk.core.network.model.OmetriaApiRequest

/**
 * Created by cristiandregan
 * on 06/08/2020.
 */

internal class Repository(
    private val client: Client,
    private val localCache: LocalCache,
    private val executor: OmetriaThreadPoolExecutor
) {

    fun flushEvents(events: List<OmetriaEvent>) {
        val apiRequest = events.toApiRequest()
        updateEvents(events)
        postEvents(apiRequest)
    }

    private fun postEvents(apiRequest: OmetriaApiRequest) {
        executor.execute {
            client.postEvents(apiRequest, success = {
                Logger.d(
                    Constants.Logger.EVENTS,
                    "Successfully flushed ${apiRequest.events?.size} events"
                )

                removeEvents(apiRequest.events)
            }, error = {
                Logger.e(Constants.Logger.EVENTS, it.detail ?: "Unknown error")
            })
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

    private fun updateEvents(events: List<OmetriaEvent>) {
        localCache.updateEvents(events)
    }

    private fun removeEvents(events: List<OmetriaEvent>?) {
        events ?: return

        localCache.removeEvents(events)
    }
}