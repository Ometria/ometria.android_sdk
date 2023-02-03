package com.android.sample.service

import com.android.ometriasdk.notification.OmetriaFirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * A service that intercepts Firebase's push messages and new token generation
 * Note: Extends OmetriaFirebaseMessagingService and calls super on overridden base methods, needed be Ometria SDK
 * in order to automatically track push notifications related events.
 */
class SampleFirebaseMessagingService : OmetriaFirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}