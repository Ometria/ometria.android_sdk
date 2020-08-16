package com.android.ometriasdk.core

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.ometriasdk.core.event.EventHandler
import com.android.ometriasdk.core.event.OmetriaBasket
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.ometriasdk.core.network.RetrofitBuilder
import com.android.ometriasdk.lifecycle.OmetriaActivityLifecycleHelper
import com.android.ometriasdk.notification.NotificationHandler
import com.google.firebase.messaging.RemoteMessage
import java.util.*

/**
 * Created by cristiandregan
 * on 08/07/2020.
 */

private val TAG = Ometria::class.simpleName

class Ometria private constructor() {

    private lateinit var ometriaConfig: OmetriaConfig
    private var isInitialized = false
    private lateinit var localCache: LocalCache
    private lateinit var eventHandler: EventHandler
    private lateinit var repository: Repository

    /**
     * Kotlin Object ensures thread safety.
     */
    private object HOLDER {
        val INSTANCE = Ometria()
    }

    companion object {
        private val instance: Ometria by lazy { HOLDER.INSTANCE }

        @JvmStatic
        fun initialize(application: Application, apiKey: String, notificationIcon: Int): Ometria {

            return instance.also {
                it.ometriaConfig = OmetriaConfig(application, apiKey, notificationIcon)
                it.localCache = LocalCache(application)
                it.isInitialized = true
                it.repository = Repository(
                    RetrofitBuilder.getOmetriaApi(it.ometriaConfig), it.localCache
                )
                it.eventHandler = EventHandler(application, it.repository)

                it.generateInstallationId()

                val activityLifecycleHelper = OmetriaActivityLifecycleHelper(it.repository)

                val lifecycle = ProcessLifecycleOwner.get().lifecycle
                lifecycle.addObserver(activityLifecycleHelper)

                application.registerActivityLifecycleCallbacks(activityLifecycleHelper)
            }
        }

        @JvmStatic
        fun instance(): Ometria {
            if (!instance.isInitialized) {
                throw IllegalStateException("SDK not initialized. Please initialize before using this method.")
            }

            return instance
        }
    }

    internal fun generateInstallationId() {
        if (repository.getInstallationId() != null) return

        val installationId = UUID.randomUUID().toString()

        repository.saveinstallationId(installationId)
    }

    fun loggingEnabled(enableDebugging: Boolean): Ometria {
        ometriaConfig.enableDebugging = enableDebugging
        Logger.setLevel(VERBOSE)

        return instance
    }

    fun onMessageReceived(remoteMessage: RemoteMessage) {
        NotificationHandler.showNotification(
            remoteMessage,
            ometriaConfig.context,
            ometriaConfig.notificationIcon
        )

        trackNotificationReceivedEvent(remoteMessage.messageId)
    }

    fun onNewToken(token: String) {
        trackPushTokenRefreshedEvent(token)
    }

    private fun trackEvent(
        type: OmetriaEventType,
        data: Map<String, Any>? = null
    ) {
        eventHandler.processEvent(type, data)
    }

    internal fun trackAppInstalledEvent() {
        trackEvent(OmetriaEventType.APP_INSTALLED)
    }

    internal fun trackAppLaunchedEvent() {
        trackEvent(OmetriaEventType.APP_LAUNCHED)
    }

    internal fun trackAppForegroundedEvent() {
        trackEvent(OmetriaEventType.APP_FOREGROUNDED)
    }

    internal fun trackAppBackgroundedEvent() {
        trackEvent(OmetriaEventType.APP_BACKGROUNDED)
    }

    fun trackScreenViewedEvent(screenName: String?, additionalInfo: Map<String, Any> = mapOf()) {
        val data = additionalInfo.toMutableMap()
        data["page"] = screenName ?: ""
        trackEvent(
            OmetriaEventType.SCREEN_VIEWED,
            data
        )
    }

    fun trackProfileIdentifiedByEmailEvent(email: String) {
        trackEvent(OmetriaEventType.PROFILE_IDENTIFIED, mapOf("email" to email))
    }

    fun trackProfileIdentifiedByCustomerIdEvent(customerId: String) {
        trackEvent(OmetriaEventType.PROFILE_IDENTIFIED, mapOf("customerId" to customerId))
    }

    fun trackProfileDeidentifiedEvent() {
        trackEvent(OmetriaEventType.PROFILE_DEIDENTIFIED)
    }

    fun trackProductViewedEvent(productId: String) {
        trackEvent(OmetriaEventType.PRODUCT_VIEWED, mapOf("productId" to productId))
    }

    fun trackProductCategoryViewedEvent(category: String) {
        trackEvent(OmetriaEventType.PRODUCT_CATEGORY_VIEWED, mapOf("category" to category))
    }

    fun trackWishlistAddedToEvent(productId: String) {
        trackEvent(OmetriaEventType.WISH_LIST_ADDED_TO, mapOf("productId" to productId))
    }

    fun trackWishlistRemovedFromEvent(productId: String) {
        trackEvent(OmetriaEventType.WISHLIST_REMOVED_FROM, mapOf("productId" to productId))
    }

    fun trackBasketViewedEvent() {
        trackEvent(OmetriaEventType.BASKET_VIEWED)
    }

    fun trackBasketUpdatedEvent(basket: OmetriaBasket) {
        trackEvent(OmetriaEventType.BASKET_UPDATED, mapOf("basket" to basket))
    }

    fun trackOrderCompletedEvent(orderId: String, basket: OmetriaBasket) {
        trackEvent(
            OmetriaEventType.ORDER_COMPLETED,
            mapOf("orderId" to orderId, "basket" to basket)
        )
    }

    fun trackPushTokenRefreshedEvent(pushToken: String) {
        trackEvent(OmetriaEventType.PUSH_TOKEN_REFRESHED, mapOf("pushToken" to pushToken))
    }

    fun trackNotificationReceivedEvent(notificationId: String?) {
        trackEvent(
            OmetriaEventType.NOTIFICATION_RECEIVED,
            mapOf("notificationId" to (notificationId ?: ""))
        )
    }

    fun trackNotificationInteractedEvent(notificationId: String) {
        trackEvent(
            OmetriaEventType.NOTIFICATION_INTERACTED,
            mapOf("notificationId" to notificationId)
        )
    }

    fun trackDeepLinkOpenedEvent(link: String, page: String) {
        trackEvent(
            OmetriaEventType.DEEP_LINK_OPENED,
            mapOf("link" to link, "page" to page)
        )
    }

    fun trackCustomEvent(customEventType: String, additionalInfo: Map<String, Any>) {
        val data = additionalInfo.toMutableMap()
        data["customEventType"] = customEventType
        trackEvent(
            OmetriaEventType.CUSTOM,
            data
        )
    }
}