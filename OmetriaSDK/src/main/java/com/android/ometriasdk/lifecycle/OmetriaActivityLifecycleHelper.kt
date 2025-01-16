package com.android.ometriasdk.lifecycle

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.Repository
import com.android.ometriasdk.util.isOlderThanAWeek
import java.util.concurrent.atomic.AtomicBoolean

internal class OmetriaActivityLifecycleHelper(
    var repository: Repository,
    private val context: Context
) :
    DefaultLifecycleObserver,
    Application.ActivityLifecycleCallbacks {

    private val firstLaunch = AtomicBoolean(false)
    private val trackedApplicationLifecycleEvents = AtomicBoolean(false)

    // DefaultLifecycleObserver

    /**
     * Using lifecycle's observer onCreate callback to decide if is first launch
     */
    override fun onCreate(owner: LifecycleOwner) {
        if (!trackedApplicationLifecycleEvents.getAndSet(true)) {
            firstLaunch.set(true)
        }
    }

    /**
     * Using lifecycle's observer onStart callback to track Launch Application event
     * and Bring Application to Foreground event
     */
    override fun onStart(owner: LifecycleOwner) {
        if (repository.isFirstAppRun()) {
            Ometria.instance().trackAppInstalledEvent()
            repository.saveIsFirstAppRun(false)
        }

        if (firstLaunch.get()) {
            Ometria.instance().trackAppLaunchedEvent()
        }

        Ometria.instance().trackAppForegroundedEvent()

        !firstLaunch.getAndSet(false)
    }

    /**
     * Using lifecycle's observer onStop callback to track Application Backgrounded event
     */
    override fun onStop(owner: LifecycleOwner) {
        Ometria.instance().trackAppBackgroundedEvent()

        if (repository.getLastPushTokenRefreshTimestamp() == 0L
            || repository.getLastPushTokenRefreshTimestamp().isOlderThanAWeek()
        ) {
            Ometria.instance().trackPushTokenRefreshedEvent(repository.getPushToken())
            repository.saveLastPushTokenRefreshTimestamp(System.currentTimeMillis())
        }
    }

    // ActivityLifecycleCallbacks

    /**
     * Using activity created callback to track Open Deep Link event
     */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        trackDeepLink(activity)
    }

    private fun trackDeepLink(activity: Activity) {
        val intent = activity.intent
        if (intent == null || intent.data == null) {
            return
        }
    }

    /**
     * Using activity started callback to track Screen View event
     */
    override fun onActivityStarted(activity: Activity) {}

    /**
     * Using activity resumed callback to update Notifications opt-in/out
     */
    override fun onActivityResumed(activity: Activity) {
        val areNotificationsEnabled =
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        if (areNotificationsEnabled != repository.areNotificationsEnabled() || repository.isFirstPermissionsUpdateEvent()) {
            repository.saveAreNotificationsEnabled(areNotificationsEnabled)
            Ometria.instance().trackPermissionsUpdateEvent(areNotificationsEnabled)
        }
    }

    /**
     * Not included in the SDK's current requirements
     */
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
