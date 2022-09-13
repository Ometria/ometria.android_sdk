package com.android.ometriasdk.core.event

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
    PRODUCT_LISTING_VIEWED("productListingViewed"),
    BASKET_VIEWED("basketViewed"),
    BASKET_UPDATED("basketUpdated"),
    CHECKOUT_STARTED("checkoutStarted"),
    ORDER_COMPLETED("orderCompleted"),
    HOME_SCREEN_VIEWED("homeScreenViewed"),

    // Notification related events
    PUSH_TOKEN_REFRESHED("pushTokenRefreshed"),
    NOTIFICATION_RECEIVED("notificationReceived"),
    NOTIFICATION_INTERACTED("notificationInteracted"),
    PERMISSION_UPDATE("permissionsUpdate"),

    // Other event types
    DEEP_LINK_OPENED("deepLinkOpened"),
    ERROR_OCCURRED("errorOccurred"),
    CUSTOM("customEvent")
}