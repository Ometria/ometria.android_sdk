package com.android.ometriasdk.core.event

/**
 * Created by cristiandregan
 * on 24/07/2020.
 */

internal enum class OmetriaEventType(var id: String) {
    // Application related events
    APP_INSTALLED("appInstalled"),
    APP_LAUNCHED("appLaunched"),
    APP_FOREGROUNDED("appForegrounded"),
    APP_BACKGROUNDED("appBackgrounded"),
    SCREEN_VIEWED("screenViewedExplicit"),
    SCREEN_VIEWED_AUTOMATIC("screenViewedAutomatic"),
    PROFILE_IDENTIFIED("profileIdentified"),
    PROFILE_DEIDENTIFIED("profileDeidentified"),

    // Product related events
    PRODUCT_VIEWED("productViewed"),
    PRODUCT_CATEGORY_VIEWED("productCategoryViewed"),
    WISH_LIST_ADDED_TO("wishlistAddedTo"),
    WISHLIST_REMOVED_FROM("wishlistRemovedFrom"),
    BASKET_VIEWED("basketViewed"),
    BASKET_UPDATED("basketUpdated"),
    ORDER_COMPLETED("orderCompleted"),

    // Notification related events
    PUSH_TOKEN_REFRESHED("pushTokenRefreshed"),
    NOTIFICATION_RECEIVED("notificationReceived"),
    NOTIFICATION_INTERACTED("notificationInteracted"),

    // Other event types
    DEEP_LINK_OPENED("deepLinkOpened"),
    CUSTOM("customEvent")
}