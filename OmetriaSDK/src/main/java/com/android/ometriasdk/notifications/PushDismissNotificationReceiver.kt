package com.android.ometriasdk.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.ometriasdk.core.Logger

/**
 * Created by cristiandregan
 * on 16/07/2020.
 */

private val TAG = PushDismissNotificationReceiver::class.simpleName

class PushDismissNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action != null && action == PUSH_DISMISS_ACTION) {
            // TODO track push notification dismissed
            Logger.d(TAG, "push dismissed")
        }
    }
}