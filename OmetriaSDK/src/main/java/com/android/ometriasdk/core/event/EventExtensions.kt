package com.android.ometriasdk.core.event

import android.content.Context
import android.os.Bundle
import androidx.core.content.pm.PackageInfoCompat
import com.android.ometriasdk.core.LocalCache
import org.json.JSONObject

/**
 * Created by cristiandregan
 * on 28/07/2020.
 */

internal fun Event.toCachedEvent(
    context: Context,
    localCache: LocalCache
): CachedEvent {
    val applicationID = context.packageName
    val installmentID = localCache.getInstallmentID()

    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val applicationVersion = packageInfo.versionName
    val buildNumber = PackageInfoCompat.getLongVersionCode(packageInfo).toString()

    return CachedEvent(
        this,
        applicationID = applicationID,
        installmentID = installmentID,
        applicationVersion = applicationVersion,
        buildNumber = buildNumber
    )
}

internal fun CachedEvent.toJson(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("type", type)
    jsonObject.put("value", value)
    jsonObject.put("params", params.toJson())

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

private fun Bundle.toJson(): JSONObject {
    val bundleJsonObject = JSONObject()
    val keys = this.keySet()

    keys.forEach {
        bundleJsonObject.put(it, JSONObject.wrap(this.get(it)))
    }

    return bundleJsonObject
}