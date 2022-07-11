package com.android.ometriasdk.notification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.network.OmetriaThreadPoolExecutor
import com.android.ometriasdk.core.network.toOmetriaNotificationBody
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.net.URL

/**
 * Created by cristiandregan
 * on 15/07/2020.
 */

const val KEY_TITLE = "title"
const val KEY_BODY = "body"
const val KEY_OMETRIA = "ometria"

internal class NotificationHandler(
    context: Context,
    notificationIcon: Int,
    notificationColor: Int?,
    notificationChannelName: String,
    private val executor: OmetriaThreadPoolExecutor
) {

    private val ometriaPushNotification: OmetriaPushNotification = OmetriaPushNotification(
        context = context,
        notificationIcon = notificationIcon,
        notificationColor = notificationColor,
        notificationChannelName = notificationChannelName
    )

    /**
     * Handles a received push notification
     * We should handle and display the push notification only if the [remoteMessage] contains the [KEY_OMETRIA] object
     */
    fun handleNotification(remoteMessage: RemoteMessage) {
        val ometriaNotificationString = remoteMessage.data[KEY_OMETRIA]
        ometriaNotificationString ?: return

        val ometriaNotificationBody = ometriaNotificationString.toOmetriaNotificationBody()
        ometriaNotificationBody.context?.let {
            Ometria.instance().trackNotificationReceivedEvent(it)
        }

        val title = remoteMessage.data[KEY_TITLE]
        val body = remoteMessage.data[KEY_BODY]

        if (ometriaNotificationBody.imageUrl != null) {
            loadImage(ometriaNotificationBody.imageUrl) {
                ometriaPushNotification.createPushNotification(
                    title = title,
                    body = body,
                    image = it,
                    ometriaNotificationBody = ometriaNotificationBody,
                    collapseId = remoteMessage.collapseKey
                )
            }
        } else {
            ometriaPushNotification.createPushNotification(
                title = title,
                body = body,
                ometriaNotificationBody = ometriaNotificationBody,
                collapseId = remoteMessage.collapseKey
            )
        }
    }

    private fun loadImage(stringUrl: String?, success: (Bitmap?) -> Unit) {
        executor.execute {
            val url = URL(stringUrl)
            var bitmap: Bitmap? = null
            try {
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: IOException) {
                Logger.e(Constants.Logger.PUSH_NOTIFICATIONS, e.message, e)
                success(null)
            }

            success(bitmap)
        }
    }
}