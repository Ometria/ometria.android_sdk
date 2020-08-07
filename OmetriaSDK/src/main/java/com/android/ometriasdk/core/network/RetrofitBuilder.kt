package com.android.ometriasdk.core.network

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

    private fun getRetrofit(): Retrofit {

        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val ometriaApi: OmetriaApi = getRetrofit().create(OmetriaApi::class.java)
}