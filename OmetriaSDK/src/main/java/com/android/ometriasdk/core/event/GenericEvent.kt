package com.android.ometriasdk.core.event

import android.os.Build
import com.android.ometriasdk.BuildConfig
import org.json.JSONObject

/**
 * Created by cristiandregan
 * on 28/07/2020.
 */

internal class GenericEvent(
    // ToDo Decide on how to manage time zone
    event: Event,
    private val applicationID: String? = null,
    private val installmentID: String? = null,
    private val applicationVersion: String? = null,
    private val buildNumber: String? = null,
    private val sdkVersion: String? = BuildConfig.VERSION_NAME,
    private val platform: String = "Android",
    private val osVersion: String? = Build.VERSION.RELEASE,
    private val deviceManufacturer: String? = Build.MANUFACTURER,
    private val deviceModel: String? = Build.MODEL
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

    override fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("type", type)
        jsonObject.put("value", value)
        jsonObject.put("params", params)

        jsonObject.put("creationDate", creationDate)
        jsonObject.put("flushDate", flushDate)
        jsonObject.put("isFlushed", isFlushed)
        jsonObject.put("isAutomaticallyTracked", isAutomaticallyTracked)

        jsonObject.put("applicationID", applicationID)
        jsonObject.put("installmentID", installmentID)
        jsonObject.put("applicationVersion", applicationVersion)
        jsonObject.put("buildNumber", buildNumber)
        jsonObject.put("sdkVersion", sdkVersion)
        jsonObject.put("platform", platform)
        jsonObject.put("osVersion", osVersion)
        jsonObject.put("deviceManufacturer", deviceManufacturer)
        jsonObject.put("deviceModel", deviceModel)

        return jsonObject
    }
}