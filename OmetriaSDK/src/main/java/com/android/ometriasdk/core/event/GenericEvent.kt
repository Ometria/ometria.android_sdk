package com.android.ometriasdk.core.event

import android.os.Build
import com.android.ometriasdk.BuildConfig
import java.util.*

/**
 * Created by cristiandregan
 * on 28/07/2020.
 */

internal class GenericEvent(
    // ToDo Decide on how to manage time zone
    // ToDo make event transient to avoid serialization
    event: Event,
    private val applicationID: String? = null,
    private val installmentID: String? = null,
    private val applicationVersion: String? = null,
    private val buildNumber: String? = null,
    private val sdkVersion: String? = BuildConfig.VERSION_NAME,
    private val platform: String = "Android",
    private val osVersion: String? = Build.VERSION.RELEASE,
    private val deviceManufacturer: String? = Build.MANUFACTURER,
    private val deviceModel: String? = Build.MODEL,
    private val creationDate: Long = Date().time,
    private val flushDate: Long = Date().time,
    private val isFlushed: Boolean = false,
    private val isAutomaticallyTracked: Boolean? = false
) : Event(event.type, event.value, event.params) {
    override fun toString(): String {
        return "Event(type=$type, value=$value, params=$params), " +
                "GenericEvent(applicationID=$applicationID, " +
                "installmentID=$installmentID, " +
                "applicationVersion=$applicationVersion, " +
                "buildNumber=$buildNumber, " +
                "sdkVersion=$sdkVersion, " +
                "platform='$platform', " +
                "osVersion=$osVersion, " +
                "deviceManufacturer=$deviceManufacturer, " +
                "deviceModel=$deviceModel, " +
                "creationDate=$creationDate, " +
                "flushDate=$flushDate, " +
                "isFlushed=$isFlushed, " +
                "isAutomaticallyTracked=$isAutomaticallyTracked)"
    }
}