package com.android.sample

import android.app.Application
import com.android.ometriasdk.core.Ometria

/**
 * Created by cristiandregan
 * on 08/07/2020.
 */

class SampleApp : Application() {
    companion object {
        lateinit var instance: SampleApp
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // Initializing Ometria SDK with application context, api token and notifications icon resource id
        // Note: Replace api token with your own
        Ometria.initialize(
            this,
            "YOUR_API_TOKEN",
            R.mipmap.ic_launcher
        ).loggingEnabled(true)
    }
}