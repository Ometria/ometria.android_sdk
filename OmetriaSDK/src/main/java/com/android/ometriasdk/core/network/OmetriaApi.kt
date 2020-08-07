package com.android.ometriasdk.core.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Created by cristiandregan
 * on 03/08/2020.
 */

internal interface OmetriaApi {

    @Headers("X-Ometria-Auth: validation-only-test-key")
    @POST("mobile-events/validate")
    fun postEventsValidate(@Body request: Any): Call<PostEventsValidateResponse>
}