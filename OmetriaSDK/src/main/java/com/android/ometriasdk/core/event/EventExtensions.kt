package com.android.ometriasdk.core.event

import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import org.json.JSONObject
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by cristiandregan
 * on 28/07/2020.
 */

internal fun OmetriaEvent.batchIdentifier(): Int {
    return (appId + appBuildNumber + appVersion + osVersion).hashCode()
}

internal fun List<OmetriaEvent>.toApiRequest(): OmetriaApiRequest {
    val dateFormat: DateFormat =
        SimpleDateFormat(Constants.Date.API_DATE_FORMAT, Locale.getDefault())
    val ometriaEvent = this.first()

    return OmetriaApiRequest(
        appId = ometriaEvent.appId,
        appVersion = ometriaEvent.appVersion,
        installationId = ometriaEvent.installationId,
        appBuildNumber = ometriaEvent.appBuildNumber,
        sdkVersion = ometriaEvent.sdkVersion,
        osVersion = ometriaEvent.osVersion,
        timestampSent = dateFormat.format(Calendar.getInstance().time),
        events = this
    )
}

internal fun OmetriaEvent.toJson(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("eventId", eventId)
    jsonObject.put("isBeingFlushed", isBeingFlushed)
    jsonObject.put("timestampOccurred", timestampOccurred)
    jsonObject.put("isAutomaticallyTracked", isAutomaticallyTracked)
    jsonObject.put("appId", appId)
    jsonObject.put("installationId", installationId)
    jsonObject.put("appId", appId)
    jsonObject.put("appVersion", appVersion)
    jsonObject.put("sdkVersion", sdkVersion)
    jsonObject.put("platform", platform)
    jsonObject.put("osVersion", osVersion)
    jsonObject.put("deviceManufacturer", deviceManufacturer)
    jsonObject.put("deviceModel", deviceModel)
    jsonObject.put("type", type)
    jsonObject.put("data", data?.dataToJson())

    return jsonObject
}

private fun Map<String, Any>.dataToJson(): JSONObject {
    val mapJsonObject = JSONObject()
    val keys = this.keys

    this.forEach { (key, value) ->
        if (value is OmetriaBasket) {

        } else {
            mapJsonObject.put(key, JSONObject.wrap(value))
        }
    }

    return mapJsonObject
}

internal fun OmetriaBasket.toJson(): JSONObject {
    val jsonObject = JSONObject()

    return jsonObject
}

internal fun JSONObject.toOmetriaEvent() = OmetriaEvent(
    getString("eventId"),
    getBoolean("isBeingFlushed"),
    getString("timestampOccurred"),
    getBoolean("isAutomaticallyTracked"),
    getString("appId"),
    getString("installationId"),
    getString("appId"),
    getString("appVersion"),
    getString("sdkVersion"),
    getString("platform"),
    getString("osVersion"),
    getString("deviceManufacturer"),
    getString("deviceModel"),
    getString("type"),
    JSONObject("data").toMap()
)

internal fun JSONObject.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()

    val keysItr: Iterator<String> = this.keys()
    while (keysItr.hasNext()) {
        val key = keysItr.next()
        var value: Any = this.get(key)

        if (value is JSONObject) {
            value = value.toMap()
        }
        map[key] = value
    }
    return map
}