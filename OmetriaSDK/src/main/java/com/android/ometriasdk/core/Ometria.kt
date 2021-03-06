package com.android.ometriasdk.core

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ProcessLifecycleOwner
import com.android.ometriasdk.core.Constants.Params.BASKET
import com.android.ometriasdk.core.Constants.Params.CLASS
import com.android.ometriasdk.core.Constants.Params.CUSTOMER_ID
import com.android.ometriasdk.core.Constants.Params.CUSTOM_EVENT_TYPE
import com.android.ometriasdk.core.Constants.Params.EMAIL
import com.android.ometriasdk.core.Constants.Params.EXTRA
import com.android.ometriasdk.core.Constants.Params.LINK
import com.android.ometriasdk.core.Constants.Params.LISTING_ATTRIBUTES
import com.android.ometriasdk.core.Constants.Params.LISTING_TYPE
import com.android.ometriasdk.core.Constants.Params.MESSAGE
import com.android.ometriasdk.core.Constants.Params.NOTIFICATIONS
import com.android.ometriasdk.core.Constants.Params.NOTIFICATION_CONTEXT
import com.android.ometriasdk.core.Constants.Params.ORDER_ID
import com.android.ometriasdk.core.Constants.Params.ORIGINAL_MESSAGE
import com.android.ometriasdk.core.Constants.Params.PAGE
import com.android.ometriasdk.core.Constants.Params.PRODUCT_ID
import com.android.ometriasdk.core.Constants.Params.PROPERTIES
import com.android.ometriasdk.core.Constants.Params.PUSH_TOKEN
import com.android.ometriasdk.core.event.EventHandler
import com.android.ometriasdk.core.event.OmetriaBasket
import com.android.ometriasdk.core.event.OmetriaEventType
import com.android.ometriasdk.core.network.Client
import com.android.ometriasdk.core.network.ConnectionFactory
import com.android.ometriasdk.core.network.OmetriaThreadPoolExecutor
import com.android.ometriasdk.lifecycle.OmetriaActivityLifecycleHelper
import com.android.ometriasdk.notification.NotificationHandler
import com.android.ometriasdk.notification.OmetriaNotificationInteractionHandler
import com.google.firebase.messaging.RemoteMessage
import java.util.*

/**
 * Created by cristiandregan
 * on 08/07/2020.
 */

/**
 * The primary class that allows instantiating and integrating Ometria in your application
 */
@Suppress("unused")
class Ometria private constructor() : OmetriaNotificationInteractionHandler {

    private lateinit var ometriaConfig: OmetriaConfig
    private var isInitialized = false
    private lateinit var localCache: LocalCache
    private lateinit var eventHandler: EventHandler
    private lateinit var repository: Repository
    private lateinit var notificationHandler: NotificationHandler
    private lateinit var executor: OmetriaThreadPoolExecutor
    lateinit var notificationInteractionHandler: OmetriaNotificationInteractionHandler

    /**
     * Kotlin Object ensures thread safety.
     */
    private object HOLDER {
        val INSTANCE = Ometria()
    }

    companion object {
        private val instance: Ometria by lazy { HOLDER.INSTANCE }

        /**
         * Initializes the singleton instance of Ometria with the given api token.
         * @return An initialized Ometria instance object. You can always get the initialized
         * instance by calling instance().
         *
         * @param application The application context.
         * @param apiToken The api key that has been attributed to your project.
         * @param notificationIcon The icon that will be used when displaying push notifications.
         */
        @JvmStatic
        fun initialize(
            application: Application,
            apiToken: String,
            notificationIcon: Int
        ) = instance.also {
            it.ometriaConfig = OmetriaConfig(apiToken)
            it.localCache = LocalCache(application)
            it.executor = OmetriaThreadPoolExecutor()
            it.repository = Repository(
                Client(ConnectionFactory(it.ometriaConfig)),
                it.localCache,
                it.executor
            )
            it.eventHandler = EventHandler(application, it.repository)
            it.notificationHandler =
                NotificationHandler(application, notificationIcon, it.executor)
            it.isInitialized = true
            it.notificationInteractionHandler = instance

            if (it.shouldGenerateInstallationId()) {
                it.generateInstallationId()
            }

            val activityLifecycleHelper = OmetriaActivityLifecycleHelper(it.repository, application)

            val lifecycle = ProcessLifecycleOwner.get().lifecycle
            lifecycle.addObserver(activityLifecycleHelper)

            application.registerActivityLifecycleCallbacks(activityLifecycleHelper)
        }

        /**
         * Instances are safe to store, since they're immutable and always the same.
         * @return An existing Ometria instance
         */
        @JvmStatic
        fun instance(): Ometria {
            if (!instance.isInitialized) {
                throw IllegalStateException("SDK not initialized. Please initialize before using this method.")
            }

            return instance
        }
    }

    private fun shouldGenerateInstallationId(): Boolean = repository.getInstallationId() == null

    internal fun generateInstallationId() {
        val installationId = UUID.randomUUID().toString()

        repository.saveInstallationId(installationId)
    }

    /**
     * This allows enabling or disabling runtime logs.
     *
     * Note: All logging is disabled by default. This is only required
     * when you encounter issues with the SDK and you want to debug it.
     */
    fun loggingEnabled(enableDebugging: Boolean): Ometria {
        ometriaConfig.enableDebugging = enableDebugging
        Logger.setLevel(VERBOSE)

        return instance
    }

    fun onMessageReceived(remoteMessage: RemoteMessage) {
        notificationHandler.handleNotification(remoteMessage)
    }

    fun onNewToken(token: String) {
        trackPushTokenRefreshedEvent(token)
    }

    private fun trackEvent(type: OmetriaEventType, data: Map<String, Any>? = null) {
        eventHandler.processEvent(type, data?.toMutableMap())
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

    /**
     * Tracks the event of a new screen being displayed.
     *
     * Note: Tracking a users independent screen views helps us track engagement of a user with the app, as well as
     * where they are in a journey. An analogous event on a website would be to track independent page views.
     *
     * @param screenName The name of the screen
     * @param additionalInfo A map containing any key value pairs that provide valuable information to your platform
     */
    fun trackScreenViewedEvent(screenName: String, additionalInfo: Map<String, Any> = mapOf()) {
        val data = mutableMapOf<String, Any>()
        data[EXTRA] = additionalInfo
        data[PAGE] = screenName
        trackEvent(OmetriaEventType.SCREEN_VIEWED, data)
    }

    internal fun trackAutomatedScreenViewedEvent(
        screenName: String?,
        additionalInfo: Map<String, Any> = mapOf()
    ) {
        val data = additionalInfo.toMutableMap()
        data[PAGE] = screenName.orEmpty()
        trackEvent(OmetriaEventType.SCREEN_VIEWED_AUTOMATIC, data)
    }

    /**
     * Tracks the current app user being identified by customerId.
     * An app user has just identified themselves. This basically means: a user has logged in.
     *
     * Note: If you don't have a customerId, you can use the alternate version of this method: trackProfileIdentifiedByEmailEvent(email: String).
     *
     * This event is absolutely pivotal to the functioning of the SDK, so take care to send it as early
     * as possible. It is not mutually exclusive with sending an profile identified
     * by e-mail event: send either event as soon as you have the information, for optimal integration.
     *
     * @param customerId The ID reserved for a particular user in your database.
     */
    fun trackProfileIdentifiedByCustomerIdEvent(customerId: String) {
        trackEvent(OmetriaEventType.PROFILE_IDENTIFIED, mapOf(CUSTOMER_ID to customerId))
    }

    /**
     * Tracks the current app user being identified by email.
     * Having a customerId makes profile matching more robust. It is not mutually exclusive with sending
     * an profile identified by customerId event: send either event as soon as you have the information, for optimal integration.
     *
     * @param email: The email by which you identify a particular user in your database.
     */
    fun trackProfileIdentifiedByEmailEvent(email: String) {
        trackEvent(OmetriaEventType.PROFILE_IDENTIFIED, mapOf(EMAIL to email))
    }

    /**
     * Track the current app user being deidentified.
     * An app user has deidentified themselves. This basically means: a user has logged out.
     *
     * Note: calling this method will cause the installation ID to be reset, and the SDK will log an event to send the new id.
     */
    fun trackProfileDeidentifiedEvent() {
        trackEvent(OmetriaEventType.PROFILE_DEIDENTIFIED)
    }

    /**
     * Track whenever a visitor clicks / taps / views / highlights or otherwise shows interest in a product.
     *
     * @param productId: The unique identifier for the product that has been interacted with.
     */
    fun trackProductViewedEvent(productId: String) {
        trackEvent(OmetriaEventType.PRODUCT_VIEWED, mapOf(PRODUCT_ID to productId))
    }

    /**
     * Track whenever a visitor clicks / taps / views / highlights or otherwise shows interest in a product listing.
     * @param listingType A string representing the type of the listing. Can be category or search or other.
     * @param listingAttributes A map containing the parameters associated with the listing. Can contain a category id or a search query for example.
     */
    fun trackProductListingViewedEvent(
        listingType: String? = null,
        listingAttributes: Map<String, Any> = mapOf()
    ) {
        val data = mutableMapOf<String, Any>()
        listingType?.let { data[LISTING_TYPE] = it }
        data[LISTING_ATTRIBUTES] = listingAttributes
        trackEvent(OmetriaEventType.PRODUCT_LISTING_VIEWED, data)
    }

    /**
     * Track when a user has added a product to their wishlist.
     * @param productId The unique identifier of the product that has been added to the wishlist.
     */
    fun trackWishlistAddedToEvent(productId: String) {
        trackEvent(OmetriaEventType.WISH_LIST_ADDED_TO, mapOf(PRODUCT_ID to productId))
    }

    /**
     * Track when a user has removed a product to their wishlist.
     * @param productId The unique identifier of the product that has been removed from the wishlist.
     */
    fun trackWishlistRemovedFromEvent(productId: String) {
        trackEvent(OmetriaEventType.WISHLIST_REMOVED_FROM, mapOf(PRODUCT_ID to productId))
    }

    /**
     * Track when the user has viewed a dedicated page, screen or modal with the contents of the shopping basket.
     */
    fun trackBasketViewedEvent() {
        trackEvent(OmetriaEventType.BASKET_VIEWED)
    }

    /**
     * Track when the user has changed their shopping basket content.
     * @param basket An OmetriaBasket object with all the available details of the current basket contents.
     */
    fun trackBasketUpdatedEvent(basket: OmetriaBasket) {
        trackEvent(OmetriaEventType.BASKET_UPDATED, mapOf(BASKET to basket))
    }

    /**
     * Track when the user has started the checkout process.
     * @param orderId The id that your system generated for the order.
     */
    fun trackCheckoutStartedEvent(orderId: String) {
        trackEvent(OmetriaEventType.CHECKOUT_STARTED, mapOf(ORDER_ID to (orderId)))
    }

    /**
     * Track when an order has been completed and paid for.
     * @param orderId The id that your system generated for the completed order.
     * @param basket An OmetriaBasket object containing all the items in the order and also the total pricing and currency
     */
    fun trackOrderCompletedEvent(orderId: String, basket: OmetriaBasket? = null) {
        val data = mutableMapOf<String, Any>()
        data[ORDER_ID] = orderId
        basket?.let { data[BASKET] = it }
        trackEvent(OmetriaEventType.ORDER_COMPLETED, data)
    }

    /**
     * Track when the user has viewed the "home page" or landing screen of your app.
     */
    fun trackHomeScreenViewedEvent() {
        trackEvent(OmetriaEventType.HOME_SCREEN_VIEWED)
    }

    internal fun trackPushTokenRefreshedEvent(pushToken: String?) {
        trackEvent(
            OmetriaEventType.PUSH_TOKEN_REFRESHED,
            mapOf(PUSH_TOKEN to (pushToken.orEmpty()))
        )
    }

    internal fun trackNotificationReceivedEvent(context: Map<String, Any>) {
        trackEvent(OmetriaEventType.NOTIFICATION_RECEIVED, mapOf(NOTIFICATION_CONTEXT to context))
    }

    internal fun trackNotificationInteractedEvent(context: Map<String, Any>) {
        trackEvent(
            OmetriaEventType.NOTIFICATION_INTERACTED,
            mapOf(NOTIFICATION_CONTEXT to context)
        )
    }

    internal fun trackPermissionsUpdateEvent(hasPermission: Boolean) {
        val permissionValue = if (hasPermission) "opt-in" else "opt-out"
        trackEvent(OmetriaEventType.PERMISSION_UPDATE, mapOf(NOTIFICATIONS to permissionValue))
    }

    /**
     * Track whenever a deep/universal link is opened in the app.
     * @param link A string representing the URL that has been opened.
     * @param page A string representing the name of the screen that has been opened as a result of decomposing the URL.
     */
    fun trackDeepLinkOpenedEvent(link: String, page: String) {
        trackEvent(OmetriaEventType.DEEP_LINK_OPENED, mapOf(LINK to link, PAGE to page))
    }

    internal fun trackErrorOccurredEvent(
        errorClass: String,
        errorMessage: String?,
        originalMessage: Map<String, Any>
    ) {
        trackEvent(
            OmetriaEventType.ERROR_OCCURRED, mapOf(
                CLASS to errorClass,
                MESSAGE to errorMessage.orEmpty(),
                ORIGINAL_MESSAGE to originalMessage
            )
        )
    }

    /**
     * Track any specific flows or pages that are of interest to the marketing department.
     * @param customEventType A string representing the name of the custom event.
     * @param additionalInfo A map containing any key value pairs that provide valuable information to your platform.
     */
    fun trackCustomEvent(customEventType: String, additionalInfo: Map<String, Any> = mapOf()) {
        val data = mutableMapOf<String, Any>()
        data[PROPERTIES] = additionalInfo
        data[CUSTOM_EVENT_TYPE] = customEventType
        trackEvent(OmetriaEventType.CUSTOM, data)
    }

    /**
     * Uploads tracked events data to the Ometria server.
     * By default, tracked events are flushed to the Ometria servers every time it reaches a limit of 10 events,
     * but no earlier than 10 seconds from the last flush operation. You only need to call this
     * method manually if you want to force a flush at a particular moment.
     */
    fun flush() {
        eventHandler.flushEvents()
    }

    /**
     * Clears all the events from local cache.
     */
    fun clear() {
        localCache.clearEvents()
    }

    override fun onDeepLinkInteraction(context: Context, deepLink: String) {
        Logger.d(Constants.Logger.PUSH_NOTIFICATIONS, "Open URL: $deepLink")
        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.data = Uri.parse(deepLink)
        context.startActivity(intent)

        trackDeepLinkOpenedEvent(deepLink, "Browser")
    }
}