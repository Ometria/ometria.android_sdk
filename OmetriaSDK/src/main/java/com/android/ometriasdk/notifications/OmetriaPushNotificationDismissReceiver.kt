package com.android.ometriasdk.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Created by cristiandregan
 * on 16/07/2020.
 */

private val TAG = OmetriaPushNotificationDismissReceiver::class.simpleName

class OmetriaPushNotificationDismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action != null && action == PUSH_DISMISS_ACTION) {
            // TODO track push notification dismissed
            Log.d(TAG, "push dismissed")
        }
    }
}