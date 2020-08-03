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

        Ometria.initialize(this, "API_KEY", R.mipmap.ic_launcher)
            .enableDebugging(true)
    }
}