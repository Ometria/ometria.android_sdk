package com.android.ometriasdk.core

import android.os.Bundle
import java.util.*

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

data class Event(
    val type: String,
    val value: String,
    // ToDo Figure it out how to manage time zone
    private val creationDate: Long = Date().time,
    private val params: Bundle = Bundle(),
    val isFlushed: Boolean = false
) {
    fun param(key: String, value: String) {
        params.putString(key, value)
    }
}