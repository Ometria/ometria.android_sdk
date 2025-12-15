package com.android.ometriasdk.notification

/**
 * An object that exposes the content of a received notification.
 *
 * @param deepLinkActionUrl The URL that was sent in the notification. We append tracking parameters to the URL
 * specified in the account and campaign settings (these can be changed in the Ometria app).
 * @param imageUrl The image URL that was sent in the notification.
 * @param campaignType Can be trigger, mass, transactional (currently only trigger is used).
 * @param externalCustomerId The id of the contact that was specified at customer creation (in the mobile app) or ingesting to Ometria.
 * @param sendId Unique id of the message.
 * @param tracking A map that contains all tracking fields specified in the account and campaign
 * settings (can be changed in the Ometria app, uses some defaults if not specified).
 */
data class OmetriaNotification(
    val deepLinkActionUrl: String?,
    val imageUrl: String?,
    val miniImageUrl: String?,
    val campaignType: String?,
    val externalCustomerId: String?,
    val sendId: String?,
    val tracking: Map<String, Any>?
) {
    override fun toString(): String {
        return "deepLinkActionUrl: $deepLinkActionUrl\n\n" +
                "imageUrl: $imageUrl\n\n" +
                "campaignType: $campaignType\n\n" +
                "externalCustomerId: $externalCustomerId\n\n" +
                "sendId: $sendId\n\n" +
                "tracking: $tracking"
    }
}