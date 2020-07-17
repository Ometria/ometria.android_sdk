package com.android.ometriasdk.core

import android.os.Bundle
import java.util.*

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

data class Event(
    val name: String,
    // ToDo Figure it out how to manage time zone
    val timeStamp: Long = Date().time,
    val bundle: Bundle = Bundle()
) {
    fun param(key: String, value: String) {
        bundle.putString(key, value)
    }
}