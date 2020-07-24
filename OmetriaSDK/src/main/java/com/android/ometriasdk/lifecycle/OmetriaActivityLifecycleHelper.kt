package com.android.ometriasdk.lifecycle

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.ometriasdk.core.Ometria
import com.android.ometriasdk.core.event.OmetriaEventType
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by cristiandregan
 * on 07/07/2020.
 */

class OmetriaActivityLifecycleHelper :
    DefaultLifecycleObserver,
    Application.ActivityLifecycleCallbacks {

    private val firstLaunch = AtomicBoolean(false)
    private val trackedApplicationLifecycleEvents = AtomicBoolean(false)

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
        if (firstLaunch.get()) {
            Ometria.instance().trackEvent(OmetriaEventType.LAUNCH_APPLICATION)
        }

        !firstLaunch.getAndSet(false)

        Ometria.instance().trackEvent(OmetriaEventType.BRING_APPLICATION_TO_FOREGROUND)
    }

    /**
     * Using lifecycle's observer onStop callback to track Application Backgrounded event
     */
    override fun onStop(owner: LifecycleOwner) {
        Ometria.instance().trackEvent(OmetriaEventType.SEND_APPLICATION_TO_BACKGROUND)
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

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

        Ometria.instance().trackEvent(OmetriaEventType.OPEN_DEEP_LINK)
    }

    /**
     * Using activity started callback to track Screen View event
     */
    override fun onActivityStarted(activity: Activity) {
        Ometria.instance()
            .trackEvent(OmetriaEventType.VIEW_SCREEN, activity::class.simpleName)
    }

    /**
     * Not included in the SDK's current requirements
     */
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}