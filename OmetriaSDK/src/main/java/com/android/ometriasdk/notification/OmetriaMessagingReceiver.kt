package com.android.ometriasdk.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.Ometria.Companion.clearOldInstance
import com.android.ometriasdk.core.network.toOmetriaNotificationBody
import com.google.firebase.messaging.RemoteMessage


class OmetriaMessagingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val remoteMessage = RemoteMessage(intent?.extras)

        try {
            Ometria.instance()
        } catch (e: IllegalStateException) {
            context?.let {
                Ometria.initializeForInternalUsage(context)

                if (Ometria.instance().isReactNativeUsage()) {
                    val ometriaNotificationBody = remoteMessage.toOmetriaNotificationBody()
                    val ometriaNotificationContext = ometriaNotificationBody?.context

                    ometriaNotificationContext?.let {
                        Ometria.instance().trackNotificationReceivedEvent(it)
                        clearOldInstance()
                    }
                }
            }
        }
    }
}