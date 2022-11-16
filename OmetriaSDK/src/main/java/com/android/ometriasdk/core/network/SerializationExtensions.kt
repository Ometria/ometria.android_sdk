package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.event.OmetriaBasket
import com.android.ometriasdk.core.event.OmetriaBasketItem
import com.android.ometriasdk.core.event.OmetriaEvent
import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import com.android.ometriasdk.notification.OmetriaNotificationBody
import org.json.JSONArray
import org.json.JSONObject

internal fun Collection<OmetriaEvent>.toJson(): JSONArray {
    val jsonArray = JSONArray()

    forEach {
        jsonArray.put(it.toJson())
    }

    return jsonArray
}

internal fun OmetriaEvent.toJson(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("eventId", eventId)
    jsonObject.put("isBeingFlushed", isBeingFlushed)
    jsonObject.put("dtOccurred", dtOccurred)
    jsonObject.put("appId", appId)
    jsonObject.put("installationId", installationId)
    jsonObject.put("appVersion", appVersion)
    jsonObject.put("appBuildNumber", appBuildNumber)
    jsonObject.put("sdkVersion", sdkVersion)
    jsonObject.put("platform", platform)
    jsonObject.put("osVersion", osVersion)
    jsonObject.put("deviceManufacturer", deviceManufacturer)
    jsonObject.put("deviceModel", deviceModel)
    jsonObject.put("type", type)
    jsonObject.put("data", data?.dataToJson() ?: JSONObject())
    jsonObject.put("sdkVersionRN", sdkVersionRN)

    return jsonObject
}

internal fun Map<String, Any>.dataToJson(): JSONObject {
    val mapJsonObject = JSONObject()
    this.forEach { (key, value) ->
        val dataValue = if (value is OmetriaBasket) value.toJson() else JSONObject.wrap(value)
        mapJsonObject.put(key, dataValue)
    }

    return mapJsonObject
}

internal fun OmetriaBasket.toJson(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("totalPrice", totalPrice)
    jsonObject.put("currency", currency)
    jsonObject.put("link", link)
    jsonObject.put("id", id)

    val jsonArray = JSONArray()

    items.forEach {
        jsonArray.put(it.toJson())
    }

    jsonObject.put("items", jsonArray)

    return jsonObject
}

internal fun OmetriaBasketItem.toJson(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("productId", productId)
    jsonObject.put("sku", sku)
    jsonObject.put("quantity", quantity)
    jsonObject.put("price", price)
    jsonObject.put("variantId", variantId)

    return jsonObject
}

internal fun OmetriaApiRequest.toJson(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("appId", appId)
    jsonObject.put("appVersion", appVersion)
    jsonObject.put("installationId", installationId)
    jsonObject.put("appBuildNumber", appBuildNumber)
    jsonObject.put("sdkVersion", sdkVersion)
    jsonObject.put("platform", platform)
    jsonObject.put("osVersion", osVersion)
    jsonObject.put("deviceManufacturer", deviceManufacturer)
    jsonObject.put("deviceModel", deviceModel)
    jsonObject.put("dtSent", dtSent)
    jsonObject.put("sdkVersionRN", sdkVersionRN)
    jsonObject.put("events", events?.toAPIJson())

    return jsonObject
}

internal fun Collection<OmetriaEvent>.toAPIJson(): JSONArray {
    val jsonArray = JSONArray()

    forEach {
        jsonArray.put(it.toAPIJson())
    }

    return jsonArray
}

internal fun OmetriaEvent.toAPIJson(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("eventId", eventId)
    jsonObject.put("dtOccurred", dtOccurred)
    jsonObject.put("type", type)
    jsonObject.put("data", data?.dataToJson())

    return jsonObject
}

internal fun OmetriaNotificationBody.toJson(): JSONObject {
    val jsonObject = JSONObject()
    jsonObject.put("imageUrl", imageUrl)
    jsonObject.put("deepLinkActionUrl", deepLinkActionUrl)
    jsonObject.put("context", context?.dataToJson() ?: JSONObject())

    return jsonObject
}