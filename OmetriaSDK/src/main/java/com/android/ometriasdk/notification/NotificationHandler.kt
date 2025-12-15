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
import java.net.URL

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
    fun handleNotification(
        remoteMessage: RemoteMessage,
        shouldDisplayNotification: Boolean = true
    ) {
        val ometriaNotificationBody = remoteMessage.toOmetriaNotificationBody() ?: return

        ometriaNotificationBody.context?.let {
            Ometria.instance().trackNotificationReceivedEvent(it)
        }

        if (!shouldDisplayNotification) return

        val title = remoteMessage.data[KEY_TITLE]
        val body = remoteMessage.data[KEY_BODY]

        loadImage(ometriaNotificationBody.miniImageUrl) { notificationLargeIcon ->
            loadImage(ometriaNotificationBody.imageUrl) { notificationImage ->
                ometriaPushNotification.createPushNotification(
                    title = title,
                    body = body,
                    notificationLargeIcon = notificationLargeIcon,
                    notificationImage = notificationImage,
                    ometriaNotificationBody = ometriaNotificationBody,
                    collapseId = remoteMessage.collapseKey
                )
            }
        }
    }

    private fun loadImage(stringUrl: String?, success: (Bitmap?) -> Unit) {
        if (stringUrl.isNullOrEmpty()) {
            success(null)
            return
        }

        executor.execute {
            var bitmap: Bitmap? = null

            try {
                val url = URL(stringUrl)
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: Throwable) {
                Logger.e(Constants.Logger.PUSH_NOTIFICATIONS, e.message, e)
            }

            success(bitmap)
        }
    }
}