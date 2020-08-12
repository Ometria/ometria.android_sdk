package com.android.ometriasdk.core.event

import android.content.Context
import com.android.ometriasdk.core.LocalCache
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.network.ApiCallback
import com.android.ometriasdk.core.network.PostEventsValidateResponse
import com.android.ometriasdk.core.network.Repository
import java.io.File
import java.io.FileOutputStream

/**
 * Created by cristiandregan
 * on 27/07/2020.
 */

private val TAG = EventHandler::class.simpleName
private const val BATCH_LIMIT = 2

internal class EventHandler(
    private val context: Context,
    private val localCache: LocalCache,
    private val repository: Repository
) {

    fun processEvent(event: OmetriaEvent) {
        sendEvent(event)
    }

    private fun sendEvent(cachedEvent: OmetriaEvent) {
        localCache.saveEvent(cachedEvent)
        flushEvents()
        Logger.d(TAG, "Track event: ", cachedEvent)
        writeEventToFile(cachedEvent)
    }

    private fun writeEventToFile(event: OmetriaEvent) {
        val path = context.getExternalFilesDir(null)

        val letDirectory = File(path, "Events")
        letDirectory.mkdirs()
        val file = File(letDirectory, "Events.txt")

        FileOutputStream(file, true).use {
            it.write("- $event\n".toByteArray())
        }
    }

    private fun flushEvents() {
        val eventsSet = localCache.getEvents()

        if (shouldFlush(eventsSet)) {
            repository.postEventsValidate(
                eventsSet!!.toCachedEventList(),
                object : ApiCallback<PostEventsValidateResponse> {
                    override fun onSuccess(response: PostEventsValidateResponse) {
                        Logger.d(TAG, "Successfully flushed")
                    }

                    override fun onError(error: String?) {
                        Logger.d(TAG, error ?: "")
                    }
                })
        }
    }

    private fun shouldFlush(eventsSet: Set<String>?): Boolean {
        return !eventsSet.isNullOrEmpty() && eventsSet.size >= BATCH_LIMIT
    }
}