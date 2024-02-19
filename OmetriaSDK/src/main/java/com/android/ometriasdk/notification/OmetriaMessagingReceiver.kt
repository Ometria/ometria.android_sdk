package com.android.ometriasdk.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.ometriasdk.core.Constants.Logger.PUSH_NOTIFICATIONS
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.network.toOmetriaNotificationBody
import com.google.firebase.messaging.RemoteMessage

class OmetriaMessagingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Logger.d(PUSH_NOTIFICATIONS, "OmetriaMessagingReceiver - onReceive")

        val remoteMessage = RemoteMessage(intent?.extras)

        try {
            Ometria.instance()
        } catch (e: IllegalStateException) {
            context?.let {
                val ometria = Ometria.initializeForInternalUsage(context)

                if (ometria.isReactNativeUsage()) {
                    val ometriaNotificationBody = remoteMessage.toOmetriaNotificationBody()
                    val ometriaNotificationContext = ometriaNotificationBody?.context
                    ometriaNotificationContext?.let {
                        ometria.trackNotificationReceivedEvent(it)
                    }
                }
            }
        }
    }
}