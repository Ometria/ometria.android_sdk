package com.android.sample.service

import android.util.Log
import com.android.ometriasdk.notifications.OmetriaFirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by cristiandregan
 * on 09/07/2020.
 */

private val TAG = SampleFirebaseMessagingService::class.simpleName

class SampleFirebaseMessagingService : OmetriaFirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "$remoteMessage")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Token: $token")
    }
}