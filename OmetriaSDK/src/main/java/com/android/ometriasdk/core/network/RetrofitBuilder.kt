package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.OmetriaConfig
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by cristiandregan
 * on 03/08/2020.
 */

internal object RetrofitBuilder {

    private const val BASE_URL = " https://mobile-events.ometria.com/v1/"

    private fun getRetrofit(ometriaConfig: OmetriaConfig): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addNetworkInterceptor(SessionInterceptor(ometriaConfig))
        okHttpClient.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create(getGson()))
            .build()
    }

    fun getOmetriaApi(ometriaConfig: OmetriaConfig): OmetriaApi {
        return getRetrofit(ometriaConfig).create(OmetriaApi::class.java)
    }

    private fun getGson(): Gson {
        return Gson().newBuilder().excludeFieldsWithoutExposeAnnotation().create()
    }
}