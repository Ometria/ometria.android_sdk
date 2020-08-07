package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.AppConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * Created by cristiandregan
 * on 07/08/2020.
 */

internal class SessionInterceptor(private val appConfig: AppConfig) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()

        builder.addHeader("X-Ometria-Auth", appConfig.apiKey)

        return chain.proceed(builder.build())
    }
}