package com.android.ometriasdk.core.event

/**
 * Created by cristiandregan
 * on 24/07/2020.
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
    RESET_APPLICATION_IDENTIFICATION("RESET_APPLICATION_IDENTIFICATION"),

    // Notification related events
    REFRESH_PUSH_TOKEN("REFRESH_PUSH_TOKEN"),
    RECEIVE_NOTIFICATION("RECEIVE_NOTIFICATION"),
    TAP_ON_NOTIFICATION("TAP_ON_NOTIFICATION"),

    // Other event types
    OPEN_DEEP_LINK("OPEN_DEEP_LINK"),
    CUSTOM("")
}