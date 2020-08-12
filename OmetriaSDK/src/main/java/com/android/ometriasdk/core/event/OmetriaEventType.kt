package com.android.ometriasdk.core.event

/**
 * Created by cristiandregan
 * on 24/07/2020.
 */

internal enum class OmetriaEventType(var id: String) {
    // Product related events
    PRODUCT_VIEWED("productViewed"),
    BASKET_VIEWED("basketViewed"),
    START_CHECKOUT("START_CHECKOUT"),
    ORDER_COMPLETED("orderCompleted"),
    WISH_LIST_ADDED_TO("wishlistAddedTo"),
    WISHLIST_REMOVED_FROM("wishlistRemovedFrom"),
    BASKET_UPDATE("basketUpdated"),
    PRODUCT_CATEGORY_VIEWED("productCategoryViewed"),

    // Application related events
    SCREEN_VIEWED("screenViewed"),
    APP_INSTALLED("appInstalled"),
    APP_LAUNCHED("appLaunched"),
    APP_FOREGROUNDED("appForegrounded"),
    APP_BACKGROUNDED("appBackgrounded"),
    PROFILE_IDENTIFIED("profileIdentified"),
    PROFILE_DEIDENTIFIED("profileDeidentified"),

    // Notification related events
    PUSH_TOKEN_REFRESHED("pushTokenRefreshed"),
    NOTIFICATION_RECEIVED("notificationReceived"),
    NOTIFICATION_INTERACTED("notificationInteracted"),

    // Other event types
    DEEP_LINK_OPENED("deepLinkOpened"),
    CUSTOM("customEvent")
}