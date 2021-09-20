package com.android.ometriasdk.notification

/**
 * An object that exposes the content of a received notification.
 *
 * @param deepLink
 * @param imageUrl
 * @param campaignType
 * @param externalCustomerId
 * @param sendId
 * @param tracking
 */
data class OmetriaNotification(
    val deepLink: String?,
    val imageUrl: String?,
    val campaignType: String?,
    val externalCustomerId: String?,
    val sendId: String?,
    val tracking: Map<String, Any>?
)