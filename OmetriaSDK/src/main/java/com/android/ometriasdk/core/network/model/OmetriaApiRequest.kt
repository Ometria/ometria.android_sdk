package com.android.ometriasdk.core.network.model

import android.os.Build
import com.android.ometriasdk.BuildConfig
import com.android.ometriasdk.core.event.OmetriaEvent
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by cristiandregan
 * on 13/08/2020.
 */
internal data class OmetriaApiRequest(
    @Expose
    @SerializedName("appId")
    val appId: String? = null,
    @Expose
    @SerializedName("appVersion")
    val appVersion: String? = null,
    @Expose
    @SerializedName("installationId")
    val installationId: String? = null,
    @Expose
    @SerializedName("appBuildNumber")
    val appBuildNumber: String? = null,
    @Expose
    @SerializedName("sdkVersion")
    val sdkVersion: String? = null,
    @Expose
    @SerializedName("platform")
    val platform: String = "Android",
    @Expose
    @SerializedName("osVersion")
    val osVersion: String? = null,
    @Expose
    @SerializedName("deviceManufacturer")
    val deviceManufacturer: String? = Build.MANUFACTURER,
    @Expose
    @SerializedName("deviceModel")
    val deviceModel: String? = Build.MODEL,
    @Expose
    @SerializedName("timestampSent")
    val timestampSent: String? = null,
    @Expose
    @SerializedName("events")
    val events: List<OmetriaEvent>? = null
)