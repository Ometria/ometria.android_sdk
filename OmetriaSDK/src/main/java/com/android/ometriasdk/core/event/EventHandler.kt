package com.android.ometriasdk.core.event

import android.content.Context
import com.android.ometriasdk.core.LocalCache
import com.android.ometriasdk.core.Logger
import java.io.File
import java.io.FileOutputStream

/**
 * Created by cristiandregan
 * on 27/07/2020.
 */

private val TAG = EventHandler::class.simpleName

internal class EventHandler(private val context: Context, private val localCache: LocalCache) {

    fun processEvent(event: Event) {
        sendEvent(event.toCachedEvent(context, localCache))
    }

    private fun sendEvent(cachedEvent: CachedEvent) {
        localCache.saveEvent(cachedEvent)
        Logger.d(TAG, "Track event: ", cachedEvent)
        writeEventToFile(cachedEvent)
    }

    private fun writeEventToFile(event: CachedEvent) {
        val path = context.getExternalFilesDir(null)

        val letDirectory = File(path, "Events")
        letDirectory.mkdirs()
        val file = File(letDirectory, "Events.txt")

        FileOutputStream(file, true).use {
            it.write("- $event\n".toByteArray())
        }
    }
}