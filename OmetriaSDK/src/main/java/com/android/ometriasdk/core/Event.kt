package com.android.ometriasdk.core

import android.os.Bundle
import com.android.ometriasdk.BuildConfig
import java.util.*

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

enum class OmetriaEventType(var id: String) {
    // Product related events
    VIEW_PRODUCT("VIEW_PRODUCT"),
    ADD_PRODUCT_TO_CART("ADD_PRODUCT_TO_CART"),
    REMOVE_PRODUCT_FROM_CART("REMOVE_PRODUCT_FROM_CART"),
    VIEW_CART("VIEW_CART"),
    START_CHECKOUT("START_CHECKOUT"),
    COMPLETE_ORDER("COMPLETE_ORDER"),
    ADD_PRODUCT_TO_WISHLIST("ADD_PRODUCT_TO_WISHLIST"),
    REMOVE_PRODUCT_FROM_WISHLIST("REMOVE_PRODUCT_FROM_WISHLIST"),
    ADD_PRODUCT_TO_CART_FROM_WISHLIST("ADD_PRODUCT_TO_CART_FROM_WISHLIST"),

    // Application related events
    VIEW_SCREEN("VIEW_SCREEN"),
    INSTALL_APPLICATION("INSTALL_APPLICATION"),
    LAUNCH_APPLICATION("LAUNCH_APPLICATION"),
    BRING_APPLICATION_TO_FOREGROUND("BRING_APPLICATION_TO_FOREGROUND"),
    SEND_APPLICATION_TO_BACKGROUND("SEND_APPLICATION_TO_BACKGROUND"),
    IDENTIFY_APPLICATION("IDENTIFY_APPLICATION"),

    // Notification related events
    REFRESH_PUSH_TOKEN("REFRESH_PUSH_TOKEN"),
    RECEIVE_NOTIFICATION("RECEIVE_NOTIFICATION"),
    TAP_ON_NOTIFICATION("TAP_ON_NOTIFICATION"),

    // Other event types
    OPEN_DEEP_LINK("OPEN_DEEP_LINK"),
    CUSTOM("")
}

open class BaseEvent(
    // ToDo Decide on how to manage time zone
    private val applicationID: String? = null,
    private val applicationVersion: String? = null,
    private val buildNumber: String? = null,
    private val sdkVersion: String? = BuildConfig.VERSION_NAME,
    private val osType: String = "Android",
    private val creationDate: Long = Date().time,
    private val flushDate: Long = Date().time,
    private val isFlushed: Boolean = false
)

data class Event(
    val type: OmetriaEventType,
    val value: String?,
    private val params: Bundle = Bundle()
) : BaseEvent() {
    fun param(key: String, value: String) {
        params.putString(key, value)
    }
}