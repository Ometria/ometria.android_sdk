package com.android.ometriasdk.core

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Created by cristiandregan
 * on 07/07/2020.
 */

private val TAG = OmetriaActivityLifecycleHelper::class.simpleName

class OmetriaActivityLifecycleHelper :
    Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver {

    private val firstLaunch = AtomicBoolean(false)
    private val trackedApplicationLifecycleEvents = AtomicBoolean(false)

    /**
     * Using lifecycle's observer onCreate callback to track Application Opened event
     */
    override fun onCreate(owner: LifecycleOwner) {
        if (!trackedApplicationLifecycleEvents.getAndSet(true)) {
            firstLaunch.set(true)
        }
    }

    /**
     * Using lifecycle's observer onStart callback to track Application Opened event
     */
    override fun onStart(owner: LifecycleOwner) {
        if (firstLaunch.get()) {
            OmetriaLog.d(TAG, "${owner.lifecycle.currentState}, First launch")
        }

        !firstLaunch.getAndSet(false)

        OmetriaLog.d(TAG, "${owner.lifecycle.currentState}, Application opened")

        // ToDO track event
    }

    /**
     * Using lifecycle's observer onStop callback to track Application Backgrounded event
     */
    override fun onStop(owner: LifecycleOwner) {
        OmetriaLog.d(TAG, "${owner.lifecycle.currentState}, Application in background")

        // ToDO track event
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Using activity created callback to track Screen View event
     */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        OmetriaLog.d(TAG, activity.localClassName)

        // ToDO track event
    }

    /**
     * Not included in the SDK's current requirements
     */
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}