package com.android.ometriasdk.core.event

import android.os.Bundle

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

open class Event(
    val type: OmetriaEventType,
    val value: String?,
    val params: Bundle = Bundle()
) {
    fun param(key: String, value: String) {
        params.putString(key, value)
    }
}