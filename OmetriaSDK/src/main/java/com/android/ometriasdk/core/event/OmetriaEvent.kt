package com.android.ometriasdk.core.event

import android.os.Build
import com.android.ometriasdk.BuildConfig
import com.android.ometriasdk.core.Constants.Common.PLATFORM
import java.util.*

internal data class OmetriaEvent(
    val eventId: String,
    var isBeingFlushed: Boolean = false,
    val dtOccurred: String = Date().toString(),
    val appId: String? = null,
    val installationId: String? = null,
    val appVersion: String? = null,
    val appBuildNumber: String? = null,
    val sdkVersion: String? = BuildConfig.SDK_VERSION_NAME,
    val platform: String = PLATFORM,
    val osVersion: String? = Build.VERSION.RELEASE,
    val deviceManufacturer: String? = Build.MANUFACTURER,
    val deviceModel: String? = Build.MODEL,
    val type: String,
    val data: Map<String, Any>?,
    var sdkVersionRN: String? = null
)