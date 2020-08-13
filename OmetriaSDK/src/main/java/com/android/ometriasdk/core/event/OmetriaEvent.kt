package com.android.ometriasdk.core.event

import android.os.Build
import com.android.ometriasdk.BuildConfig
import com.google.gson.annotations.Expose
import java.util.*

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

internal open class OmetriaEvent(
    val creationDate: String = Date().toString(),
    val flushDate: String? = null,
    val isFlushed: Boolean = false,
    val isAutomaticallyTracked: Boolean? = false,
    val applicationID: String? = null,
    val installmentID: String? = null,
    val applicationVersion: String? = null,
    val buildNumber: String? = null,
    val sdkVersion: String? = BuildConfig.VERSION_NAME,
    val platform: String = "Android",
    val osVersion: String? = Build.VERSION.RELEASE,
    val deviceManufacturer: String? = Build.MANUFACTURER,
    val deviceModel: String? = Build.MODEL,
    @Expose
    val type: OmetriaEventType,
    val data: Map<String, Any>?
)