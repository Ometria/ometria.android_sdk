package com.android.ometriapoc.service

import android.util.Log
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.notifications.OmetriaFirebaseMessagingService
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by cristiandregan
 * on 09/07/2020.
 */

private val TAG = OmetriaPocFirebaseMessagingService::class.simpleName

class OmetriaPocFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "$remoteMessage")

        Ometria.onMessageReceived(remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Token: $token")

        Ometria.onNewToken(token)
    }
}