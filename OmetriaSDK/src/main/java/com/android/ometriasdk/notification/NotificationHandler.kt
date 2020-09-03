package com.android.ometriasdk.notification

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.android.ometriasdk.core.Constants
import com.android.ometriasdk.core.Logger
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.network.OmetriaThreadPoolExecutor
import com.android.ometriasdk.core.network.toOmetriaNotification
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
    private val executor: OmetriaThreadPoolExecutor,
    private val ometriaPushNotification: OmetriaPushNotification = OmetriaPushNotification(
        context,
        notificationIcon
    )
) {

    fun handleNotification(remoteMessage: RemoteMessage) {
        val ometriaNotificationString = remoteMessage.data[KEY_OMETRIA]
        ometriaNotificationString ?: return

        val ometriaNotification = ometriaNotificationString.toOmetriaNotification()
        ometriaNotification?.context?.let {
            Ometria.instance().trackNotificationReceivedEvent(it)
        }

        val title = remoteMessage.data[KEY_TITLE]
        val body = remoteMessage.data[KEY_BODY]

        if (ometriaNotification?.imageUrl != null) {
            loadNotificationWithIcon(
                ometriaNotification.imageUrl,
                title,
                body,
                ometriaNotification,
                remoteMessage.collapseKey
            )
        } else {
            ometriaPushNotification.createPushNotification(
                title = title,
                body = body,
                ometriaNotification = ometriaNotification,
                collapseId = remoteMessage.collapseKey
            )
        }
    }

    private fun loadNotificationWithIcon(
        stringUrl: String?,
        title: String?,
        body: String?,
        ometriaNotification: OmetriaNotification?,
        collapseId: String?
    ) {
        executor.execute {
            val url = URL(stringUrl)
            var bitmap: Bitmap? = null
            try {
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())
            } catch (e: IOException) {
                Logger.e(Constants.Logger.PUSH_NOTIFICATIONS, e.message, e)
            }

            bitmap?.let {
                ometriaPushNotification.createPushNotification(
                    title,
                    body,
                    bitmap,
                    ometriaNotification,
                    collapseId
                )
            } ?: run {
                Logger.e(
                    Constants.Logger.PUSH_NOTIFICATIONS,
                    "The notification content has missing fields or is incorrectly formatted."
                )
            }
        }
    }
}