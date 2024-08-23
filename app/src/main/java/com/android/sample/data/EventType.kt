package com.android.sample.data

/**
 * An enum for manually tracked events.
 * Note: Please check Ometria SDK official documentation in order to see all events.
 */
enum class EventType {
    SCREEN_VIEWED,
    PROFILE_IDENTIFIED_BY_EMAIL,
    PROFILE_IDENTIFIED_BY_CUSTOMER_ID,
    PROFILE_DEIDENTIFIED,
    PRODUCT_VIEWED,
    PRODUCT_LISTING_VIEWED,
    BASKET_VIEWED,
    BASKET_UPDATED,
    CHECKOUT_STARTED,
    ORDER_COMPLETED,
    HOME_SCREEN_VIEWED,
    CUSTOM,
    SIMULATE_PUSH_TOKEN_REFRESHED,
    FLUSH,
    CLEAR
}
