package com.android.ometriasdk.core

import android.content.Context

/**
 * Created by cristiandregan
 * on 16/07/2020.
 */

internal class AppConfig(
    context: Context,
    notificationIcon: Int
) {
    var context: Context? = null
    var notificationIcon: Int = 0

    init {
        this.context = context
        this.notificationIcon = notificationIcon
    }
}