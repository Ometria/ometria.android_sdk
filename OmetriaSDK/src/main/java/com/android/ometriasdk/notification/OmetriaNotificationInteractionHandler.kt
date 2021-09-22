package com.android.ometriasdk.notification

/**
 * Created by cristiandregan
 * on 09/10/2020.
 */

/**
 * An interface that allows you to control what happens when a user interacts with an Ometria originated push notification
 */
interface OmetriaNotificationInteractionHandler {
    @Deprecated(
        message = "Use the new onNotificationInteraction(ometriaNotification: OmetriaNotification) method",
    )
    fun onDeepLinkInteraction(deepLink: String) {/* default implementation */ }

    fun onNotificationInteraction(ometriaNotification: OmetriaNotification) {/* default implementation */ }
}