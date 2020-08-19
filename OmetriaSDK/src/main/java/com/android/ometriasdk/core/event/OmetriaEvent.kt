package com.android.ometriasdk.core.event

import android.os.Build
import com.android.ometriasdk.BuildConfig
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

internal data class OmetriaEvent(
    val eventId: String,
    var isBeingFlushed: Boolean = false,
    @Expose
    @SerializedName("timestampOccurred")
    val timestampOccurred: String = Date().toString(),
    val isAutomaticallyTracked: Boolean? = false,
    val appId: String? = null,
    val installationId: String? = null,
    val appVersion: String? = null,
    val appBuildNumber: String? = null,
    val sdkVersion: String? = BuildConfig.VERSION_NAME,
    val platform: String = "Android",
    val osVersion: String? = Build.VERSION.RELEASE,
    val deviceManufacturer: String? = Build.MANUFACTURER,
    val deviceModel: String? = Build.MODEL,
    @Expose
    @SerializedName("type")
    val type: String,
    @Expose
    val data: Map<String, Any>?
)