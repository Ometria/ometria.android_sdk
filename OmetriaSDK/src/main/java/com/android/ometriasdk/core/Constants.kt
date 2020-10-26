package com.android.ometriasdk.core

/**
 * Created by cristiandregan
 * on 14/08/2020.
 */

internal object Constants {
    object Date {
        const val API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZZ"
    }

    object Params {
        const val PAGE = "page"
        const val EMAIL = "email"
        const val CUSTOMER_ID = "customerId"
        const val PRODUCT_ID = "productId"
        const val BASKET = "basket"
        const val ORDER_ID = "orderId"
        const val PUSH_TOKEN = "pushToken"
        const val NOTIFICATION_CONTEXT = "context"
        const val LINK = "link"
        const val CUSTOM_EVENT_TYPE = "customEventType"
        const val CLASS = "class"
        const val MESSAGE = "message"
        const val ORIGINAL_MESSAGE = "originalMessage"
        const val PROPERTIES = "properties"
    }

    object Common {
        const val PLATFORM = "Android"
    }

    object API {
        const val HEADER_CONTENT_TYPE = "content-type"
        const val APPLICATION_JSON = "application/json"
        const val HEADER_AUTHENTICATION = "X-Ometria-Auth"
        const val API_ENDPOINT = "https://mobile-events.ometria.com/v1/mobile-events"
    }

    object Logger {
        const val UI = "UI"
        const val NETWORK = "Network"
        const val CACHE = "Cache"
        const val PUSH_NOTIFICATIONS = "PushNotifications"
        const val APPLICATION = "Application"
        const val EVENTS = "Events"
        const val GENERAL = "General"
    }
}