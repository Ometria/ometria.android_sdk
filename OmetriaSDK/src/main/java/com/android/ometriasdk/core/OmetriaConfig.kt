package com.android.ometriasdk.core

import android.app.Application

internal class OmetriaConfig(
    var apiToken: String,
    var application: Application
) {
    var enableDebugging: Boolean = false
}