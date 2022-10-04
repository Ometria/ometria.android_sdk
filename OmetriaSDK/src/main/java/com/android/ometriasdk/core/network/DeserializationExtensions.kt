package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.Constants.Logger.PUSH_NOTIFICATIONS
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.network.model.OmetriaApiError
import com.android.ometriasdk.notification.KEY_OMETRIA
import com.android.ometriasdk.notification.OmetriaNotification
import com.android.ometriasdk.notification.OmetriaNotificationBody
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

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
    val ometriaEvent = OmetriaEvent(
        jsonObject.getString("eventId"),
        jsonObject.getBoolean("isBeingFlushed"),
        jsonObject.getString("dtOccurred"),
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

    var sdkVersionRN: String? = null
    try {
        sdkVersionRN = jsonObject.getString("sdkVersionRN")
    } catch (_: JSONException) {
    }

    ometriaEvent.sdkVersionRN = sdkVersionRN

    return ometriaEvent
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

@Throws(JSONException::class)
internal fun String.toOmetriaApiError(): OmetriaApiError {
    val jsonObject = JSONObject(this)
    return OmetriaApiError(
        jsonObject.getString("detail"),
        jsonObject.getInt("status"),
        jsonObject.getString("title"),
        jsonObject.getString("type")
    )
}

internal fun String.toOmetriaNotificationBody(): OmetriaNotificationBody {
    val jsonObject = JSONObject(this)

    var context: Map<String, Any>? = null
    var deepLinkActionUrl: String? = null
    var imageUrl: String? = null

    try {
        context = jsonObject.getJSONObject("context").toMap()
    } catch (e: JSONException) {
        Logger.e(PUSH_NOTIFICATIONS, e.message, e)
        Ometria.instance().trackErrorOccurredEvent(e.javaClass.name, e.message, jsonObject.toMap())
    }
    try {
        deepLinkActionUrl = jsonObject.getString("deepLinkActionUrl")
    } catch (e: JSONException) {
        Logger.w(PUSH_NOTIFICATIONS, e.message.orEmpty())
    }
    try {
        imageUrl = jsonObject.getString("imageUrl")
    } catch (e: JSONException) {
        Logger.w(PUSH_NOTIFICATIONS, e.message.orEmpty())
    }

    return OmetriaNotificationBody(
        imageUrl,
        deepLinkActionUrl,
        context
    )
}

internal fun RemoteMessage.toOmetriaNotification(): OmetriaNotification? {
    val ometriaNotificationString = this.data[KEY_OMETRIA]
    ometriaNotificationString ?: return null

    return ometriaNotificationString.toOmetriaNotificationBody().toOmetriaNotification()
}

@Suppress("UNCHECKED_CAST")
internal fun OmetriaNotificationBody.toOmetriaNotification(): OmetriaNotification {
    val deepLinkActionUrl = this.deepLinkActionUrl
    val imageUrl = this.imageUrl
    val campaignType: String? = context?.get("campaign_type") as? String
    val externalCustomerId: String? = context?.get("ext_customer_id") as? String
    val sendId: String? = context?.get("send_id") as? String
    val tracking: Map<String, Any>? = context?.get("tracking") as? Map<String, Any>

    return OmetriaNotification(
        deepLinkActionUrl,
        imageUrl,
        campaignType,
        externalCustomerId,
        sendId,
        tracking
    )
}