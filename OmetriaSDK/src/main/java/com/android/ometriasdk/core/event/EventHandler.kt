package com.android.ometriasdk.core.event

import android.content.Context
import com.android.ometriasdk.core.Logger

/**
 * Created by cristiandregan
 * on 27/07/2020.
 */

private val TAG = EventHandler::class.simpleName

internal class EventHandler(val context: Context) {

    fun processEvent(event: Event) {
        Logger.d(TAG, "Track event: ", event)

        context.openFileOutput("Events.txt", Context.MODE_APPEND).use {
            it.write("$event\n".toByteArray())
        }
    }
}