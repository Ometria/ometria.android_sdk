package com.android.ometriasdk.core.event

import android.os.Build
import com.android.ometriasdk.BuildConfig
import java.util.*

/**
 * Created by cristiandregan
 * on 28/07/2020.
 */

internal open class CachedEvent(
    event: Event,
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
    val deviceModel: String? = Build.MODEL
) : Event(event.type, event.value, event.params) {
    override fun toString(): String {
        return "Event(type=$type, " +
                "value=$value, " +
                "params=$params, " +
                "creationDate=$creationDate, " +
                "flushDate=$flushDate, " +
                "isFlushed=$isFlushed, " +
                "isAutomaticallyTracked=$isAutomaticallyTracked), " +
                "applicationID=$applicationID, " +
                "installmentID=$installmentID, " +
                "applicationVersion=$applicationVersion, " +
                "buildNumber=$buildNumber, " +
                "sdkVersion=$sdkVersion, " +
                "platform='$platform', " +
                "osVersion=$osVersion, " +
                "deviceManufacturer=$deviceManufacturer, " +
                "deviceModel=$deviceModel)"
    }
}