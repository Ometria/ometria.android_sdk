package com.android.ometriasdk.notification

import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * A Service that extends the [FirebaseMessagingService] used to intercept push notifications and push token generation.
 * Use this as default push notifications service in your AndroidManifest file or extend it in your own Service.
 */
open class OmetriaFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Logger.d(Constants.Logger.PUSH_NOTIFICATIONS, "Notification received")

        Ometria.instance().onMessageReceived(remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Logger.d(Constants.Logger.PUSH_NOTIFICATIONS, "Token - $token")

        Ometria.instance().onNewToken(token)
    }
}