package com.android.ometriasdk.core

import android.app.Application

/**
 * Created by cristiandregan
 * on 16/07/2020.
 */

internal class OmetriaConfig(
    var apiToken: String,
    var application: Application
) {
    var enableDebugging: Boolean = false
}