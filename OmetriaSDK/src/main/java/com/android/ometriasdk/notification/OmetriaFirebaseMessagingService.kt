package com.android.ometriasdk.notification

import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

open class OmetriaFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Logger.d(Constants.Logger.PUSH_NOTIFICATIONS, "$remoteMessage")

        Ometria.instance().onMessageReceived(remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Logger.d(Constants.Logger.PUSH_NOTIFICATIONS, "Token - $token")

        Ometria.instance().onNewToken(token)
    }
}