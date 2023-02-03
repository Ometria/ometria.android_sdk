package com.android.ometriasdk.core.network.model

import android.os.Build
import com.android.ometriasdk.core.Constants.Common.PLATFORM
import com.android.ometriasdk.core.event.OmetriaEvent

internal data class OmetriaApiRequest(
    val appId: String? = null,
    val appVersion: String? = null,
    val installationId: String? = null,
    val appBuildNumber: String? = null,
    val sdkVersion: String? = null,
    val platform: String = PLATFORM,
    val osVersion: String? = null,
    val deviceManufacturer: String? = Build.MANUFACTURER,
    val deviceModel: String? = Build.MODEL,
    val dtSent: String? = null,
    val sdkVersionRN: String? = null,
    val events: List<OmetriaEvent>? = null
)