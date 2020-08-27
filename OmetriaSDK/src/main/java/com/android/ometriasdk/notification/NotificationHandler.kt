package com.android.ometriasdk.notification

import android.content.Context
import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.network.toOmetriaNotification
import com.google.firebase.messaging.RemoteMessage
import org.json.JSONException

/**
 * Created by cristiandregan
 * on 15/07/2020.
 */

const val KEY_TITLE = "title"
const val KEY_BODY = "body"
const val KEY_OMETRIA = "ometria"

internal class NotificationHandler {

    fun handleNotification(remoteMessage: RemoteMessage, context: Context, notificationIcon: Int) {
        val ometriaNotificationString = remoteMessage.data[KEY_OMETRIA]
        ometriaNotificationString ?: return

        val ometriaNotification = ometriaNotificationString.toOmetriaNotification()
        ometriaNotification?.context?.let {
            Ometria.instance().trackNotificationReceivedEvent(it)
        }

        val title = remoteMessage.data[KEY_TITLE]
        val body = remoteMessage.data[KEY_BODY]

        val ometriaPushNotification = OmetriaPushNotification(context, notificationIcon)
        ometriaPushNotification.createPushNotification(title, body, ometriaNotification)
    }
}