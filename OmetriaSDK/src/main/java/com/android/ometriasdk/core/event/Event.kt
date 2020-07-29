package com.android.ometriasdk.core.event

import android.os.Bundle
import org.json.JSONObject

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

open class Event(
    val type: OmetriaEventType,
    val value: String?,
    val params: Bundle = Bundle()
) : BaseEvent() {
    fun param(key: String, value: String) {
        params.putString(key, value)
    }

    override fun toString(): String {
        return "Event(type=$type, " +
                "value=$value, " +
                "params=$params, " +
                "creationDate=$creationDate, " +
                "flushDate=$flushDate, " +
                "isFlushed=$isFlushed, " +
                "isAutomaticallyTracked=$isAutomaticallyTracked)"
    }

    open fun toJson(): JSONObject {
        val jsonObject = JSONObject()
        jsonObject.put("type", type)
        jsonObject.put("value", value)
        jsonObject.put("params", params)

        jsonObject.put("creationDate", creationDate)
        jsonObject.put("flushDate", flushDate)
        jsonObject.put("isFlushed", isFlushed)
        jsonObject.put("isAutomaticallyTracked", isAutomaticallyTracked)

        return jsonObject
    }
}