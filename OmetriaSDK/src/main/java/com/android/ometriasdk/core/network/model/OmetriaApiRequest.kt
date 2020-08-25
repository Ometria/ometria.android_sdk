package com.android.ometriasdk.core.network.model

import android.os.Build
import com.android.ometriasdk.core.event.OmetriaEvent

/**
 * Created by cristiandregan
 * on 13/08/2020.
 */
internal data class OmetriaApiRequest(
    val appId: String? = null,
    val appVersion: String? = null,
    val installationId: String? = null,
    val appBuildNumber: String? = null,
    val sdkVersion: String? = null,
    val platform: String = "Android",
    val osVersion: String? = null,
    val deviceManufacturer: String? = Build.MANUFACTURER,
    val deviceModel: String? = Build.MODEL,
    val timestampSent: String? = null,
    val events: List<OmetriaEvent>? = null
)