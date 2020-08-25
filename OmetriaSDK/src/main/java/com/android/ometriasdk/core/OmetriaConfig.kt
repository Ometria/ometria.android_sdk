package com.android.ometriasdk.core

import android.content.Context

/**
 * Created by cristiandregan
 * on 16/07/2020.
 */

internal class OmetriaConfig(
    var context: Context,
    var apiKey: String,
    var notificationIcon: Int = 0
) {
    var enableDebugging: Boolean = false
}