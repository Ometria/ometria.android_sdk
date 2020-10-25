package com.android.ometriasdk.notification

/**
 * Created by cristiandregan
 * on 09/10/2020.
 */

/**
 * An interface that allows you to control what happens when a user interacts with an Ometria originated push notification
 */
interface OmetriaNotificationInteractionHandler {
    fun onDeepLinkInteraction(deepLink: String)
}