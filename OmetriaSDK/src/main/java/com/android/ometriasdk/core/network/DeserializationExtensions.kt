package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.network.model.OmetriaApiError
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by cristiandregan
 * on 21/08/2020.
 */

internal fun String.toOmetriaEventList(): MutableList<OmetriaEvent> {
    val jsonArray = JSONArray(this)
    val ometriaEventArray = mutableListOf<OmetriaEvent>()
    for (i in 0 until jsonArray.length()) {
        ometriaEventArray.add(jsonArray[i].toString().toOmetriaEvent())
    }

    return ometriaEventArray
}

internal fun String.toOmetriaEvent(): OmetriaEvent {
    val jsonObject = JSONObject(this)
    return OmetriaEvent(
        jsonObject.getString("eventId"),
        jsonObject.getBoolean("isBeingFlushed"),
        jsonObject.getString("dtOccurred"),
        jsonObject.getBoolean("isAutomaticallyTracked"),
        jsonObject.getString("appId"),
        jsonObject.getString("installationId"),
        jsonObject.getString("appVersion"),
        jsonObject.getString("appBuildNumber"),
        jsonObject.getString("sdkVersion"),
        jsonObject.getString("platform"),
        jsonObject.getString("osVersion"),
        jsonObject.getString("deviceManufacturer"),
        jsonObject.getString("deviceModel"),
        jsonObject.getString("type"),
        jsonObject.getJSONObject("data").toMap()
    )
}

internal fun JSONObject?.toMap(): Map<String, Any> {
    if (this == null) return mapOf()

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


internal fun String.toOmetriaApiError(): OmetriaApiError {
    val jsonObject = JSONObject(this)
    return OmetriaApiError(
        jsonObject.getString("detail"),
        jsonObject.getInt("status"),
        jsonObject.getString("title"),
        jsonObject.getString("type")
    )
}
