package com.android.ometriasdk.notification

import android.content.Context
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by cristiandregan
 * on 15/07/2020.
 */

internal object NotificationHandler {

    fun showNotification(remoteMessage: RemoteMessage, context: Context, notificationIcon: Int) {
        val ometriaPushNotification = OmetriaPushNotification(context, notificationIcon)
        ometriaPushNotification.createPushNotification(remoteMessage.toIntent())
    }
}