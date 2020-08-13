package com.android.ometriasdk.core.event

import com.android.ometriasdk.core.AppGson

/**
 * Created by cristiandregan
 * on 28/07/2020.
 */

internal fun Set<String>.toOmetriaEventList(): List<OmetriaEvent> {
    val eventsList = mutableListOf<OmetriaEvent>()

    this.sorted().forEach { cachedEventString ->
        eventsList.add(
            AppGson.instance.fromJson(
                cachedEventString,
                OmetriaEvent::class.java
            )
        )
    }

    return eventsList
}