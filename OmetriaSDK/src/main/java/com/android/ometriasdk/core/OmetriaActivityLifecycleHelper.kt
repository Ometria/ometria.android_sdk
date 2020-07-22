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

    override fun onCreate(owner: LifecycleOwner) {
        if (!trackedApplicationLifecycleEvents.getAndSet(true)) {
            firstLaunch.set(true)
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        if (firstLaunch.get()) {
            OmetriaLog.d(TAG, "${owner.lifecycle.currentState}, First launch")
        }

        !firstLaunch.getAndSet(false)

        OmetriaLog.d(TAG, "${owner.lifecycle.currentState}, Application opened")
    }

    override fun onStop(owner: LifecycleOwner) {
        OmetriaLog.d(TAG, "${owner.lifecycle.currentState}, Application in background")
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        OmetriaLog.d(TAG, activity.localClassName)
    }

    override fun onActivityStarted(activity: Activity) {
        OmetriaLog.d(TAG, activity.localClassName)
    }

    override fun onActivityResumed(activity: Activity) {
        OmetriaLog.d(TAG, activity.localClassName)
    }

    override fun onActivityPaused(activity: Activity) {
        OmetriaLog.d(TAG, activity.localClassName)
    }

    override fun onActivityStopped(activity: Activity) {
        OmetriaLog.d(TAG, activity.localClassName)
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        OmetriaLog.d(TAG, activity.localClassName)
    }

    override fun onActivityDestroyed(activity: Activity) {
        OmetriaLog.d(TAG, activity.localClassName)
    }
}