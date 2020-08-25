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

        Ometria.initialize(
            this,
            "pk_test_IY2XfgrRsIlRGBP0rH2ks9dAbG1Ov24BsdggNTqP",
            R.mipmap.ic_launcher
        ).loggingEnabled(true)
    }
}