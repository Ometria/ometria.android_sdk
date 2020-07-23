package com.android.sample.service

import android.util.Log
import com.android.ometriasdk.core.Ometria
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by cristiandregan
 * on 09/07/2020.
 */

private val TAG = SampleFirebaseMessagingService::class.simpleName

class SampleFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "$remoteMessage")

        Ometria.instance().onMessageReceived(remoteMessage)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Token: $token")

        Ometria.instance().onNewToken(token)
    }
}