package com.android.ometriasdk.core

import android.content.Context

internal class OmetriaConfig(
    var apiToken: String,
    var application: Context
) {
    var enableDebugging: Boolean = false
}