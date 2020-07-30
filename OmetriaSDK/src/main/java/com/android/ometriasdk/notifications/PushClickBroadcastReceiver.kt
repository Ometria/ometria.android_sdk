package com.android.ometriasdk.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.event.OmetriaEventType.TAP_ON_NOTIFICATION

/**
 * Created by cristiandregan
 * on 30/07/2020.
 */
class PushClickBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (action != null && action == PUSH_TAP_ACTION) {
            val mainIntent = context!!.packageManager.getLaunchIntentForPackage(context.packageName)
            mainIntent?.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(mainIntent)

            Ometria.instance().trackEvent(TAP_ON_NOTIFICATION)
        }
    }
}