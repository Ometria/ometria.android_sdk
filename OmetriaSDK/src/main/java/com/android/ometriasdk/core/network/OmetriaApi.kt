package com.android.ometriasdk.core.network

import com.android.ometriasdk.core.network.model.OmetriaApiRequest
import com.android.ometriasdk.core.network.model.OmetriaApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by cristiandregan
 * on 03/08/2020.
 */

internal interface OmetriaApi {

    @POST("mobile-events/validate")
    fun postEventsValidate(@Body request: OmetriaApiRequest): Call<OmetriaApiResponse>
}