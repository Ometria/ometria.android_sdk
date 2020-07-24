package com.android.ometriasdk.core.event

import android.os.Bundle

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

data class Event(
    val type: OmetriaEventType,
    val value: String?,
    private val params: Bundle = Bundle()
) : BaseEvent() {
    fun param(key: String, value: String) {
        params.putString(key, value)
    }
}