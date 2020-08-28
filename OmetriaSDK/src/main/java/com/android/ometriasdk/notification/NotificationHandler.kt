package com.android.ometriasdk.notification

import android.content.Context
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.network.OmetriaThreadPoolExecutor
import com.android.ometriasdk.core.network.toOmetriaNotification
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by cristiandregan
 * on 15/07/2020.
 */

const val KEY_TITLE = "title"
const val KEY_BODY = "body"
const val KEY_OMETRIA = "ometria"

internal class NotificationHandler {

    fun handleNotification(
        remoteMessage: RemoteMessage,
        context: Context,
        notificationIcon: Int,
        executor: OmetriaThreadPoolExecutor
    ) {
        val ometriaNotificationString = remoteMessage.data[KEY_OMETRIA]
        ometriaNotificationString ?: return

        val ometriaNotification = ometriaNotificationString.toOmetriaNotification()
        ometriaNotification?.context?.let {
            Ometria.instance().trackNotificationReceivedEvent(it)
        }

        val title = remoteMessage.data[KEY_TITLE]
        val body = remoteMessage.data[KEY_BODY]

        val ometriaPushNotification = OmetriaPushNotification(context, notificationIcon, executor)
        ometriaPushNotification.createPushNotification(title, body, ometriaNotification, remoteMessage.collapseKey)
    }
}