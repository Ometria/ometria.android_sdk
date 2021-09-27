package com.android.sample

import android.app.Application
import androidx.core.content.ContextCompat
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
            R.drawable.ic_notification_nys,
            ContextCompat.getColor(this, R.color.colorAccent)
        ).loggingEnabled(true)
    }
}