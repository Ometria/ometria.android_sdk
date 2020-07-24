package com.android.ometriasdk.notifications

import com.android.ometriasdk.core.Logger
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by cristiandregan
 * on 17/07/2020.
 */

private val TAG = OmetriaFirebaseMessagingService::class.simpleName

open class OmetriaFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Logger.d(TAG, "$remoteMessage")

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Logger.d(TAG, "Token: $token")

    }
}