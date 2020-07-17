package com.android.ometriasdk.notifications

import android.content.Context
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by cristiandregan
 * on 15/07/2020.
 */
object NotificationsHandler {

    fun showNotification(remoteMessage: RemoteMessage, context: Context?, notificationIcon: Int) {
        val ometriaPushNotification = OmetriaPushNotification(context, notificationIcon)
        ometriaPushNotification.createPushNotification(remoteMessage.toIntent())
    }
}