package com.android.ometriasdk.core

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.ometriasdk.core.Constants.Params.BASKET
import com.android.ometriasdk.core.Constants.Params.CATEGORY
import com.android.ometriasdk.core.Constants.Params.CUSTOMER_ID
import com.android.ometriasdk.core.Constants.Params.CUSTOM_EVENT_TYPE
import com.android.ometriasdk.core.Constants.Params.EMAIL
import com.android.ometriasdk.core.Constants.Params.LINK
import com.android.ometriasdk.core.Constants.Params.NOTIFICATION_ID
import com.android.ometriasdk.core.Constants.Params.ORDER_ID
import com.android.ometriasdk.core.Constants.Params.PAGE
import com.android.ometriasdk.core.Constants.Params.PRODUCT_ID
import com.android.ometriasdk.core.Constants.Params.PUSH_TOKEN
import com.android.ometriasdk.core.event.EventHandler
import com.android.ometriasdk.core.event.OmetriaBasket
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.ometriasdk.core.network.Client
import com.android.ometriasdk.core.network.ConnectionFactory
import com.android.ometriasdk.core.network.OmetriaThreadPoolExecutor
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

                it.repository =
                    Repository(
                        Client(ConnectionFactory(it.ometriaConfig)),
                        it.localCache,
                        OmetriaThreadPoolExecutor()
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

        repository.saveInstallationId(installationId)
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

    fun trackScreenViewedEvent(screenName: String, additionalInfo: Map<String, Any> = mapOf()) {
        val data = additionalInfo.toMutableMap()
        data[PAGE] = screenName ?: ""
        trackEvent(
            OmetriaEventType.SCREEN_VIEWED,
            data
        )
    }

    internal fun trackAutomatedScreenViewedEvent(
        screenName: String?,
        additionalInfo: Map<String, Any> = mapOf()
    ) {
        val data = additionalInfo.toMutableMap()
        data[PAGE] = screenName ?: ""
        trackEvent(
            OmetriaEventType.AUTOMATED_SCREEN_VIEWED,
            data
        )
    }

    fun trackProfileIdentifiedByEmailEvent(email: String) {
        trackEvent(OmetriaEventType.PROFILE_IDENTIFIED, mapOf(EMAIL to email))
    }

    fun trackProfileIdentifiedByCustomerIdEvent(customerId: String) {
        trackEvent(OmetriaEventType.PROFILE_IDENTIFIED, mapOf(CUSTOMER_ID to customerId))
    }

    fun trackProfileDeidentifiedEvent() {
        trackEvent(OmetriaEventType.PROFILE_DEIDENTIFIED)
    }

    fun trackProductViewedEvent(productId: String) {
        trackEvent(OmetriaEventType.PRODUCT_VIEWED, mapOf(PRODUCT_ID to productId))
    }

    fun trackProductCategoryViewedEvent(category: String) {
        trackEvent(OmetriaEventType.PRODUCT_CATEGORY_VIEWED, mapOf(CATEGORY to category))
    }

    fun trackWishlistAddedToEvent(productId: String) {
        trackEvent(OmetriaEventType.WISH_LIST_ADDED_TO, mapOf(PRODUCT_ID to productId))
    }

    fun trackWishlistRemovedFromEvent(productId: String) {
        trackEvent(OmetriaEventType.WISHLIST_REMOVED_FROM, mapOf(PRODUCT_ID to productId))
    }

    fun trackBasketViewedEvent() {
        trackEvent(OmetriaEventType.BASKET_VIEWED)
    }

    fun trackBasketUpdatedEvent(basket: OmetriaBasket) {
        trackEvent(OmetriaEventType.BASKET_UPDATED, mapOf(BASKET to basket))
    }

    fun trackOrderCompletedEvent(orderId: String, basket: OmetriaBasket) {
        trackEvent(
            OmetriaEventType.ORDER_COMPLETED,
            mapOf(ORDER_ID to orderId, BASKET to basket)
        )
    }

    fun trackPushTokenRefreshedEvent(pushToken: String) {
        trackEvent(OmetriaEventType.PUSH_TOKEN_REFRESHED, mapOf(PUSH_TOKEN to pushToken))
    }

    fun trackNotificationReceivedEvent(notificationId: String?) {
        trackEvent(
            OmetriaEventType.NOTIFICATION_RECEIVED,
            mapOf(NOTIFICATION_ID to (notificationId ?: ""))
        )
    }

    fun trackNotificationInteractedEvent(notificationId: String) {
        trackEvent(
            OmetriaEventType.NOTIFICATION_INTERACTED,
            mapOf(NOTIFICATION_ID to notificationId)
        )
    }

    fun trackDeepLinkOpenedEvent(link: String, page: String) {
        trackEvent(
            OmetriaEventType.DEEP_LINK_OPENED,
            mapOf(LINK to link, PAGE to page)
        )
    }

    fun trackCustomEvent(customEventType: String, additionalInfo: Map<String, Any>) {
        val data = additionalInfo.toMutableMap()
        data[CUSTOM_EVENT_TYPE] = customEventType
        trackEvent(
            OmetriaEventType.CUSTOM,
            data
        )
    }
}