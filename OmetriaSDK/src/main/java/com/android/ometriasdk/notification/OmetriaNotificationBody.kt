package com.android.ometriasdk.notification

internal data class OmetriaNotificationBody(
    val imageUrl: String?,
    val miniImageUrl: String?,
    val deepLinkActionUrl: String?,
    val context: Map<String, Any>?
)